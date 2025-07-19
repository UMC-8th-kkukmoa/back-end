package kkukmoa.kkukmoa.payment.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.PaymentHandler;
import kkukmoa.kkukmoa.payment.dto.request.PaymentRequestDto;
import kkukmoa.kkukmoa.payment.dto.response.PaymentPrepareResponseDto;
import kkukmoa.kkukmoa.payment.dto.response.TossPaymentConfirmResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.payment.converter.PaymentConverter;
import kkukmoa.kkukmoa.payment.domain.Payment;
import kkukmoa.kkukmoa.payment.repository.PaymentRepository;
import kkukmoa.kkukmoa.payment.repository.RedisPaymentPrepareRepository;
import kkukmoa.kkukmoa.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentCommandService {

    private final RedisPaymentPrepareRepository redisRepo;
    private final PaymentRepository paymentRepo;
    private final RestTemplate restTemplate;
    private final AuthService authService;

    @Value("${toss.secret-key}")
    private String secretKey;

    public PaymentPrepareResponseDto prepare(PaymentRequestDto.PaymentPrepareRequestDto request) {
        String orderId = request.getOrderId() != null
                ? request.getOrderId()
                : UUID.randomUUID().toString().replace("-", "").substring(0, 20);

        PaymentRequestDto.PaymentPrepareRequestDto saveDto = new PaymentRequestDto.PaymentPrepareRequestDto(
                orderId,
                request.getOrderName(),
                request.getAmount()
        );

        redisRepo.save(saveDto);

        return new PaymentPrepareResponseDto(orderId, saveDto.getOrderName(), saveDto.getAmount());
    }

    //토스
    public Payment confirm(PaymentRequestDto.PaymentConfirmRequestDto req) {
        PaymentRequestDto.PaymentPrepareRequestDto prepare = redisRepo.findByOrderId(req.getOrderId())
                .orElseThrow(() -> new PaymentHandler(ErrorStatus.PAYMENT_INFO_NOT_FOUND));

        if (req.getAmount() != prepare.getAmount()) {
            throw new PaymentHandler(ErrorStatus.PAYMENT_AMOUNT_MISMATCH);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedKey);

        Map<String, Object> body = Map.of(
                "paymentKey", req.getPaymentKey(),
                "orderId", req.getOrderId(),
                "amount", req.getAmount()
        );

        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        ResponseEntity<TossPaymentConfirmResponseDto> response = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/confirm",
                entity,
                TossPaymentConfirmResponseDto.class
        );

        TossPaymentConfirmResponseDto res = response.getBody();
        if (res == null) {
            log.error("Toss 응답 null: {}", req);
            throw new PaymentHandler(ErrorStatus.PAYMENT_CONFIRM_RESPONSE_NULL);
        }

        User user = authService.getCurrentUser();
        Payment payment = PaymentConverter.toEntity(prepare, user);
        payment.applyTossApproval(
                res.getPaymentKey(),
                res.getMethod(),
                OffsetDateTime.parse(res.getApprovedAt()).toLocalDateTime()
        );

        return paymentRepo.save(payment);
    }
}