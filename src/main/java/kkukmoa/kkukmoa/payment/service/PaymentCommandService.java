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

    private final RedisPaymentPrepareRepository redisRepository;
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final AuthService authService;

    @Value("${toss.secret-key}")
    private String secretKey;

    public PaymentPrepareResponseDto prepare(PaymentRequestDto.PaymentPrepareRequestDto request) {
        // orderId가 비어있으면 새로 생성
        String orderId = request.getOrderId() != null
                ? request.getOrderId()
                : UUID.randomUUID().toString().replace("-", "").substring(0, 20);

        PaymentRequestDto.PaymentPrepareRequestDto saveDto =
                PaymentRequestDto.PaymentPrepareRequestDto.of(
                        orderId,
                        request.getOrderName(),
                        request.getAmount()
                );
        redisRepository.save(saveDto);

        return new PaymentPrepareResponseDto(
                saveDto.getOrderId(),
                saveDto.getOrderName(),
                saveDto.getAmount()
        );
    }

    public Payment confirm(PaymentRequestDto.PaymentConfirmRequestDto req) {
        PaymentRequestDto.PaymentPrepareRequestDto prepare = redisRepository.findByOrderId(req.getOrderId())
                .orElseThrow(() -> new PaymentHandler(ErrorStatus.PAYMENT_INFO_NOT_FOUND));

        if (req.getAmount() != prepare.getAmount()) {
            throw new PaymentHandler(ErrorStatus.PAYMENT_AMOUNT_MISMATCH);
        }

        // Toss 결제 승인 API 호출
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

        // 사용자 정보 및 결제 저장
        User user = authService.getCurrentUser();
        Payment payment = PaymentConverter.toEntity(prepare, user);
        payment.updateFromTossResponse(res); // ← Toss 응답 전체 반영 + status 변경까지

        return paymentRepository.save(payment);

    }
}
