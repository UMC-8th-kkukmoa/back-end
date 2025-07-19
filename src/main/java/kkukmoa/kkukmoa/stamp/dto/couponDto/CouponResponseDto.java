package kkukmoa.kkukmoa.stamp.dto.couponDto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class CouponResponseDto {

  @Getter
  @Builder
  @Schema(description = "쿠폰 목록 DTO")
  public static class couponListDto {

    @Schema(description = "쿠폰 목록")
    List<couponDto> coupons;
    @Schema(description = "조회된 쿠폰 수", example = "6")
    Integer total;

  }

  @Getter
  @Builder
  @Schema(description = "단일 쿠폰 정보 DTO")
  public static class couponDto{

    @Schema(description = "쿠폰 식별자", example = "1")
    Long couponId;
    @Schema(description = "가게 식별자", example = "1")
    Long storeId;
    @Schema(description = "가게 이미지",example = "https://[bucket name].s3-[aws-region].amazonaws.com")
    String storeImg;
    @Schema(description = "가게 이름", example = "미진카페")
    String storeName;
    @Schema(description = "업종", example = "카페")
    String storeType; // TODO ENUM 형태로 바꾸기
    @Schema(description = "쿠폰 이름", example = "리워드 쿠폰")
    String couponName;
  }

}
