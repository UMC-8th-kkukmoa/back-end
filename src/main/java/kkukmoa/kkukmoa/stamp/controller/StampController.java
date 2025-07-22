package kkukmoa.kkukmoa.stamp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
import kkukmoa.kkukmoa.stamp.dto.couponDto.CouponResponseDto;
import kkukmoa.kkukmoa.stamp.dto.couponDto.CouponResponseDto.couponListDto;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto.StampListDto;
import kkukmoa.kkukmoa.stamp.service.coupon.CouponCommandService;
import kkukmoa.kkukmoa.stamp.service.coupon.CouponQueryService;
import kkukmoa.kkukmoa.stamp.service.stamp.StampQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final CouponCommandService couponCommandService;

  @GetMapping("/")
  @Operation(summary = "스탬프 목록 조회 API", description = "스탬프 타입을 입력하세요. 페이징 X")
  public ApiResponse<StampResponseDto.StampListDto> stamps(@RequestParam(name = "store-type") String storeType) {
    StampListDto stampList = stampQueryService.stamList(storeType);
    return ApiResponse.onSuccess(stampList);
  }

  @GetMapping("/coupons")
  @Operation(summary = "내 쿠폰 목록 조회 API", description = "내가 소유한 쿠폰의 목록을 반환합니다.\n쿠폰의 QR코드는 Base64로 형태로 인코딩 되어있습니다.")
  public ApiResponse<CouponResponseDto.couponListDto> coupons(@RequestParam(name = "store-type") String storeType) {
    couponListDto couponListDto = couponQueryService.couponList(storeType);
    return ApiResponse.onSuccess(couponListDto);
  }

  @GetMapping("/coupons/make")
  @Operation(summary = "테스트용 쿠폰 생성 API", description = "테스트용 쿠폰 생성 API")
  public ResponseEntity<Coupon> makeCoupon() {
    return ResponseEntity.ok(couponCommandService.saveCoupon());
  }
}
