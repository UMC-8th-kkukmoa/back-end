package kkukmoa.kkukmoa.stamp.dto.couponDto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class CouponResponseDto {

    @Getter
    @Builder
    @Schema(description = "쿠폰 목록 DTO")
    public static class couponListDto {

        @Schema(description = "쿠폰 목록")
        @JsonProperty(value = "coupon_list")
        List<couponDto> coupons;

        @Schema(description = "조회된 쿠폰 수", example = "6")
        @JsonProperty(value = "total")
        Integer total;
    }

    @Getter
    @Builder
    @Schema(description = "단일 쿠폰 정보 DTO")
    public static class couponDto {

        @Schema(description = "쿠폰 식별자", example = "1")
        @JsonProperty(value = "coupon_id")
        Long couponId;

        @Schema(description = "가게 식별자", example = "1")
        @JsonProperty(value = "store_id")
        Long storeId;

        @Schema(description = "가게 이미지", example = "iVBORw0KGgoAAAANSUhEUgAAAMg...")
        @JsonProperty(value = "store_image")
        String storeImg;

        @Schema(description = "가게 이름", example = "미진카페")
        @JsonProperty(value = "store_name")
        String storeName;

        @Schema(description = "업종", example = "카페")
        @JsonProperty(value = "store_type")
        String storeType; // TODO ENUM 형태로 바꾸기

        @Schema(description = "쿠폰 이름", example = "리워드 쿠폰")
        @JsonProperty(value = "coupon_name")
        String couponName;

        @Schema(description = "쿠폰 QR 이미지")
        @JsonProperty(value = "coupon_qrcode")
        String couponQrCode;
    }
}
