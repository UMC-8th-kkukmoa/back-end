package kkukmoa.kkukmoa.payment.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.PaymentHandler;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.payment.converter.PaymentConverter;
import kkukmoa.kkukmoa.payment.domain.Payment;
import kkukmoa.kkukmoa.payment.dto.request.PaymentRequestDto;
import kkukmoa.kkukmoa.payment.dto.response.PaymentPrepareResponseDto;
import kkukmoa.kkukmoa.payment.dto.response.TossPaymentConfirmResponseDto;
import kkukmoa.kkukmoa.payment.repository.PaymentRepository;
import kkukmoa.kkukmoa.payment.repository.RedisPaymentPrepareRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.voucher.service.VoucherCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
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
    private final VoucherCommandService voucherCommandService;

    @Value("${toss.secret-key}")
    private String secretKey;

    @Transactional
    public PaymentPrepareResponseDto prepare(PaymentRequestDto.PaymentPrepareRequestDto request) {
        log.info(
                "[결제 사전 등록 요청] orderName={}, amount={}, unitPrice={}, quantity={}",
                request.getOrderName(),
                request.getAmount(),
                request.getVoucherUnitPrice(),
                request.getVoucherQuantity());

        // orderId가 비어있으면 새로 생성
        String orderId =
                request.getOrderId() != null
                        ? request.getOrderId()
                        : UUID.randomUUID().toString().replace("-", "").substring(0, 20);

        PaymentRequestDto.PaymentPrepareRequestDto saveDto =
                PaymentRequestDto.PaymentPrepareRequestDto.of(
                        orderId,
                        request.getOrderName(),
                        request.getAmount(),
                        request.getVoucherUnitPrice(),
                        request.getVoucherQuantity());
        redisRepository.save(saveDto);

        return new PaymentPrepareResponseDto(
                saveDto.getOrderId(), saveDto.getOrderName(), saveDto.getAmount());
    }

    @Transactional
    public Payment confirm(PaymentRequestDto.PaymentConfirmRequestDto req, String token) {
        log.info("[결제 확인: 토큰 기반] orderId: {}", req.getOrderId());

        User user = authService.getUserFromToken(token);
        return internalConfirm(req, user);
    }

    @Transactional
    public Payment confirm(PaymentRequestDto.PaymentConfirmRequestDto req) {
        log.info("[결제 확인: 인증 유저 기반] orderId: {}", req.getOrderId());

        User user = authService.getCurrentUser(); // 🔐 기존 방식 유지
        return internalConfirm(req, user);
    }

    private Payment internalConfirm(PaymentRequestDto.PaymentConfirmRequestDto req, User user) {
        log.info("[결제 확인] 요청 orderId: {}, amount: {}", req.getOrderId(), req.getAmount());

        // 1. Redis에서 사전 저장된 결제 정보 조회
        PaymentRequestDto.PaymentPrepareRequestDto prepare =
                redisRepository
                        .findByOrderId(req.getOrderId())
                        .orElseThrow(() -> {
                            log.error("[결제 확인 실패] Redis에서 orderId={} 정보 없음", req.getOrderId());
                            return new PaymentHandler(ErrorStatus.PAYMENT_INFO_NOT_FOUND);
                        });

        // 2. 금액 무결성 검증
        if (req.getAmount() != prepare.getAmount()) {
            log.error("[결제 금액 불일치] 요청: {}, 사전 저장: {}", req.getAmount(), prepare.getAmount());
            throw new PaymentHandler(ErrorStatus.PAYMENT_AMOUNT_MISMATCH);
        }

        // 3. Toss 결제 승인 요청
        log.info("[Toss 요청] paymentKey={}, orderId={}, amount={}",
                req.getPaymentKey(), req.getOrderId(), req.getAmount());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String encodedKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
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
            log.error("[Toss 응답] 응답 객체가 null");
            throw new PaymentHandler(ErrorStatus.PAYMENT_CONFIRM_RESPONSE_NULL);
        }

        // 4. 결제 정보 저장
        Payment payment = PaymentConverter.toEntity(prepare, user);
        payment.updateFromTossResponse(res);
        paymentRepository.save(payment);

        // 5. 금액권 분할 발급
        int unitPrice = prepare.getVoucherUnitPrice();
        int quantity = prepare.getVoucherQuantity();

        log.info("[금액권 발급 시도] orderId={}, unitPrice={}, quantity={}",
                req.getOrderId(), unitPrice, quantity);
        voucherCommandService.issueVouchersByQr(unitPrice, quantity, user, payment);

        return payment;
    }
}
