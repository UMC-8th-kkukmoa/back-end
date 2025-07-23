package kkukmoa.kkukmoa.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.common.util.swagger.ApiErrorCodeExamples;
import kkukmoa.kkukmoa.payment.domain.Payment;
import kkukmoa.kkukmoa.payment.dto.request.PaymentRequestDto;
import kkukmoa.kkukmoa.payment.dto.response.PaymentPrepareResponseDto;
import kkukmoa.kkukmoa.payment.service.PaymentCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "토스 결제 API", description = "토스 결제 API 입니다.")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/payments")
public class PaymentController {
    private final PaymentCommandService paymentService;

    @Operation(
            summary = "결제 준비 API",
            description =
                    """
                    프론트엔드에서 결제 요청 시 사전 결제 정보를 Redis에 저장하고 결제에 필요한 정보를 응답합니다.

                    - 결제 요청 정보는 10분 동안 유효합니다.
                    - 응답으로 결제에 필요한 orderId, amount, orderName 등의 정보를 제공합니다.
                    - 결제 성공 시 해당 정보를 기반으로 결제 승인 API를 호출해야 합니다.
                    """)
    @ApiErrorCodeExamples({ErrorStatus.INVALID_INPUT, ErrorStatus.INTERNAL_SERVER_ERROR})
    @PostMapping("/prepare")
    public ResponseEntity<PaymentPrepareResponseDto> prepare(
            @RequestBody PaymentRequestDto.PaymentPrepareRequestDto request) {
        log.info("Prepare 호출됨: {}", request);
        PaymentPrepareResponseDto responseDto = paymentService.prepare(request);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "결제 승인 API",
            description =
                    """
                    프론트엔드 결제 완료 후 Toss 결제 승인 API를 호출하는 단계입니다.

                    - paymentKey, orderId, amount를 바탕으로 Redis에 저장된 결제 요청 정보와 일치 여부를 검증합니다.
                    - 무결성 확인 후, 결제 완료 처리 및 결제 정보 저장.
                    - 성공 시 결제 ID를 반환합니다.
                    """)
    @ApiErrorCodeExamples({
        ErrorStatus.PAYMENT_NOT_FOUND,
        ErrorStatus.INVALID_PAYMENT_REQUEST,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<String>> confirm(
            @RequestBody PaymentRequestDto.PaymentConfirmRequestDto request) {

        log.info("Confirm 호출됨: {}", request);
        Payment payment = paymentService.confirm(request);
        return ResponseEntity.ok(ApiResponse.onSuccess("결제 성공: " + payment.getId()));
    }
}
