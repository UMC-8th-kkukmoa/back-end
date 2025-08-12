package kkukmoa.kkukmoa.stamp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.common.util.swagger.ApiErrorCodeExamples;
import kkukmoa.kkukmoa.stamp.dto.couponDto.CouponResponseDto;
import kkukmoa.kkukmoa.stamp.dto.couponDto.CouponResponseDto.couponListDto;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto.StampListDto;
import kkukmoa.kkukmoa.stamp.service.coupon.CouponCommandService;
import kkukmoa.kkukmoa.stamp.service.coupon.CouponQueryService;
import kkukmoa.kkukmoa.stamp.service.stamp.StampCommandService;
import kkukmoa.kkukmoa.stamp.service.stamp.StampQueryService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "스탬프&쿠폰", description = "스탬프와 쿠폰에 해당하는 API 목록")
@RestController
@RequestMapping("/v1/stamps")
@RequiredArgsConstructor
public class StampController {

    private final StampQueryService stampQueryService;
    private final CouponQueryService couponQueryService;
    private final StampCommandService stampCommandService;
    private final CouponCommandService couponCommandService;

    @GetMapping
    @Operation(summary = "스탬프 목록 조회 API", description = "스탬프 타입을 입력하세요.")
    @ApiErrorCodeExamples(
            value = {ErrorStatus.STORE_CATEGORY_NOT_FOUND, ErrorStatus.AUTHENTICATION_FAILED})
    public ApiResponse<StampResponseDto.StampListDto> stamps(
            @RequestParam(name = "store-type", required = false) CategoryType storeType) {
        StampListDto stampList = stampQueryService.stampList(storeType);
        return ApiResponse.onSuccess(stampList);
    }

    @GetMapping("/coupons")
    @Operation(
            summary = "내 쿠폰 목록 조회 API",
            description =
                    "내가 소유한 쿠폰의 목록을 반환합니다.\n"
                            + "쿠폰의 QR 코드 예시) coupon_a305acf6-66a9-4776-a48a-ca9d3210643e ")
    @ApiErrorCodeExamples(
            value = {ErrorStatus.AUTHENTICATION_FAILED, ErrorStatus.STORE_CATEGORY_NOT_FOUND})
    public ApiResponse<CouponResponseDto.couponListDto> coupons(
            @RequestParam(name = "store-type", required = false) CategoryType storeType) {
        couponListDto couponListDto = couponQueryService.couponList(storeType);
        return ApiResponse.onSuccess(couponListDto);
    }

    @Operation(
            summary = "스탬프 적립 API",
            description = "QR 코드 정보를 이용하여 스탬프를 적립합니다.<br>스탬프가 10개 적립되면 쿠폰을 발급합니다.")
    @ApiErrorCodeExamples({
        ErrorStatus.QR_INVALID,
        ErrorStatus.STORE_NOT_FOUND,
        ErrorStatus.AUTHENTICATION_FAILED
    })
    @PutMapping
    public ApiResponse<StampResponseDto.StampSaveDto> saveCoupon(
            @RequestParam("qr") String qrCode) {
        StampResponseDto.StampSaveDto saveDto = stampCommandService.save(qrCode);
        return ApiResponse.onSuccess(saveDto);
    }
}
