package kkukmoa.kkukmoa.payment.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.PaymentHandler;
import kkukmoa.kkukmoa.payment.dto.PaymentConfirmRequestDto;
import kkukmoa.kkukmoa.payment.dto.PaymentPrepareRequestDto;
import kkukmoa.kkukmoa.payment.dto.TossPaymentConfirmResponseDto;
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
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PaymentService {
    private final RedisPaymentPrepareRepository redisRepo;
    private final PaymentRepository paymentRepo;
    private final RestTemplate restTemplate;
    private final AuthService authService;

    @Value("${toss.secret-key}")
    private String secretKey;


    public void prepare(PaymentPrepareRequestDto request) {
        redisRepo.save(request);
    }

    public Payment confirm(PaymentConfirmRequestDto req) {
        PaymentPrepareRequestDto prepare = redisRepo.findByOrderId(req.orderId())
                .orElseThrow(() -> new PaymentHandler(ErrorStatus.PAYMENT_NOT_FOUND));


        if (req.amount() != prepare.amount()) {
            throw new PaymentHandler(ErrorStatus.PAYMENT_AMOUNT_MISMATCH);

        }

        // Toss 승인 API 호출
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedKey);

        Map<String, Object> body = Map.of(
                "paymentKey", req.paymentKey(),
                "orderId", req.orderId(),
                "amount", req.amount()
        );

        HttpEntity<?> entity = new HttpEntity<>(body, headers);
        ResponseEntity<TossPaymentConfirmResponseDto> response = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/confirm",
                entity,
                TossPaymentConfirmResponseDto.class
        );

        // DB 저장
        TossPaymentConfirmResponseDto res = response.getBody();
        if (res == null) {
            throw new PaymentHandler(PAYMENT_CONFIRM_RESPONSE_NULL); 
        User user = authService.getCurrentUser();
        Payment payment = PaymentConverter.toEntity(prepare, user);
        payment.applyTossApproval(res.getPaymentKey(), res.getMethod(), LocalDateTime.parse(res.getApprovedAt()));
        return paymentRepo.save(payment);
    }
}
