package kkukmoa.kkukmoa.voucher.dto;

import lombok.Builder;
import lombok.Getter;

public class VoucherResponseDto {
    @Getter
    @Builder
    public static class VoucherListResponseDto {

        private String name;
        private String validDays;
        private String status;
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
        private String status;
        private String qrCodeUuid;
        private String qrCode;
        private String daysLeft;
    }

    @Getter
    @Builder
    public static class VoucherDeductResponseDto {

        private String name;
        private String validDays;
        private int usedAmount;
        private int remainingValue;
    }
}
