package kkukmoa.kkukmoa.stamp.dto.couponDto;

import com.fasterxml.jackson.annotation.JsonProperty;

import kkukmoa.kkukmoa.common.enums.QrCodeType;

import lombok.Builder;
import lombok.Getter;

public class CouponUseResponseDto {

    @Getter
    @Builder
    public static class CouponUseDto {
        @JsonProperty(value = "qr_type")
        private QrCodeType qrType;

        @JsonProperty(value = "coupon_id")
        private Long couponId;
    }
}
