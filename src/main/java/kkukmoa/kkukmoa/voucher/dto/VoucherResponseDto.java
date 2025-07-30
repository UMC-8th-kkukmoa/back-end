package kkukmoa.kkukmoa.voucher.dto;

import kkukmoa.kkukmoa.stamp.enums.CouponStatus;
import lombok.Builder;
import lombok.Getter;

public class VoucherResponseDto {
    @Getter
    @Builder
    public static class VoucherListResponseDto {

        private String name;
        private String validDays;
        private CouponStatus status;
        private String qrCodeUuid;
        private String daysLeft;
    }

    @Getter
    @Builder
    public static class VoucherDetailResponseDto {
        private String name;
        private int value;
        private int remainingValue;
        private String validDays;
        private CouponStatus status;
        private String qrCodeUuid;
        private String qrCode;
        private String daysLeft;
    }
}
