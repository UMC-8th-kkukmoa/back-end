package kkukmoa.kkukmoa.owner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.common.util.swagger.ApiErrorCodeExamples;
import kkukmoa.kkukmoa.owner.dto.OwnerQrResponseDto;
import kkukmoa.kkukmoa.owner.service.OwnerQueryService;
import kkukmoa.kkukmoa.stamp.dto.couponDto.CouponUseResponseDto.CouponUseDto;
import kkukmoa.kkukmoa.stamp.service.coupon.CouponCommandService;
import kkukmoa.kkukmoa.voucher.dto.VoucherResponseDto;
import kkukmoa.kkukmoa.voucher.service.VoucherCommandService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사장님 API", description = "사장님 권한이 필요합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/owners")
public class OwnerController {

    private final OwnerQueryService ownerQueryService;
    private final CouponCommandService couponCommandService;
    private final VoucherCommandService voucherCommandService;

    @GetMapping("/qrcode/stamps")
    @Operation(
            summary = "( 사장님 ) 스탬프 적립 QR 코드 발급·조회 API",
            description =
                    """
                        고객에게 보여줄 스탬프 적립용 QR 코드를 발급 받는 API 입니다.\n
                        생성한 QR 코드의 유효 시간은 1분입니다. 1분이 지나면 해당 API를 다시 호출하여 새로운 QR 코드를 발급 받으세요.
                    """)
    @ApiErrorCodeExamples(
            value = {ErrorStatus.AUTHENTICATION_FAILED, ErrorStatus.OWNER_STORE_NOT_FOUND})
//    @PreAuthorize("hasAuthority('USER')")
    public ApiResponse<OwnerQrResponseDto.QrDto> getStampQrCode() {
        OwnerQrResponseDto.QrDto stamp = ownerQueryService.getStamp();
        return ApiResponse.onSuccess(stamp);
    }

    @GetMapping("/qrcode/category")
    @Operation(
            summary = "( 사장님 ) QR 인식 후 유형 구분",
            description =
                    """
                        고객이 보여준 QR 유형을 반환합니다.\n
                        유형에 따라 쿠폰사용/금액권사용 API를 호출해주세요.
                        금액권의 경우 balance에 잔액을 반환합니다.
                        금액권이 아닐 경우 null을 반환합니다.
                    """)
    @ApiErrorCodeExamples({
        ErrorStatus.AUTHENTICATION_FAILED,
        ErrorStatus.QR_INVALID_TYPE,
        ErrorStatus.VOUCHER_NOT_FOUND
    })
//    @PreAuthorize("hasAuthority('OWNER')")
    public ApiResponse<OwnerQrResponseDto.QrTypeDto> getQrCode(
            @RequestParam("qr-uuid") String qrCode) {

        OwnerQrResponseDto.QrTypeDto qrType = ownerQueryService.getQrType(qrCode);

        return ApiResponse.onSuccess(qrType);
    }

    @Operation(
            summary = "( 사장님 ) 금액권 일부 사용 처리 API",
            description =
                    """
                    사장님이 QR 코드 스캔 후 호출할 API 입니다.
                    금액권에서 일부 금액만 차감하여 사용하는 방식입니다.

                    - QR UUID는 접두어가 포함된 상태로 전달됩니다. (예: voucher_abc123)
                    - 차감할 금액을 함께 전송해야 합니다.
                    - 남은 금액이 0이 되면 자동으로 상태가 USED로 변경됩니다.
                    """)
    @ApiErrorCodeExamples({
        ErrorStatus.VOUCHER_ALREADY_USED,
        ErrorStatus.VOUCHER_INVALID_AMOUNT,
        ErrorStatus.VOUCHER_NOT_FOUND,
        ErrorStatus.VOUCHER_BALANCE_NOT_ENOUGH,
        ErrorStatus.QR_INVALID,
        ErrorStatus.QR_INVALID_TYPE,
        ErrorStatus.AUTHENTICATION_FAILED
    })
    @PatchMapping("/use/voucher/{qr-uuid}")
//    @PreAuthorize("hasAuthority('OWNER')")
    public ApiResponse<VoucherResponseDto.VoucherDeductResponseDto> useVoucher(
            @Parameter(
                            description =
                                    "금액권 QR UUID (예: voucher_3ad5c7c6-3c5a-4b96-a5e7-bf9201795a42)",
                            example = "voucher_3ad5c7c6-3c5a-4b96-a5e7-bf9201795a42")
                    @PathVariable("qr-uuid")
                    String qrCode,
            @Parameter(description = "차감할 금액 (원)", example = "5000") @RequestParam("amount")
                    int useAmount) {
        VoucherResponseDto.VoucherDeductResponseDto dto =
                voucherCommandService.useVoucher(qrCode, useAmount);
        return ApiResponse.onSuccess(dto);
    }

    @PatchMapping("/use/coupons/{qr-uuid}")
    @Operation(
            summary = "( 사장님 ) 서비스 쿠폰 사용 처리 API",
            description =
                    """
                    사장님이 QR 코드 스캔 후 호출할 API 입니다.

                    - QR UUID는 접두어가 포함된 상태로 전달됩니다. (예: coupon_abc123)
                    """)
    @ApiErrorCodeExamples({
        ErrorStatus.AUTHENTICATION_FAILED,
        ErrorStatus.QR_INVALID_TYPE,
        ErrorStatus.COUPON_NOT_FOUND,
        ErrorStatus.COUPON_INVALID_USED_PLACE,
        ErrorStatus.COUPON_IS_USED
    })
//    @PreAuthorize("hasAuthority('OWNER')")
    public ApiResponse<CouponUseDto> useCoupon(
            @Parameter(
                            description =
                                    "쿠폰 QR UUID (예: coupon_3ad5c7c6-3c5a-4b96-a5e7-bf9201795a42)",
                            example = "coupon_3ad5c7c6-3c5a-4b96-a5e7-bf9201795a42")
                    @PathVariable("qr-uuid")
                    String qrCode) {
        CouponUseDto dto = couponCommandService.useCoupon(qrCode);
        return ApiResponse.onSuccess(dto);
    }
}
