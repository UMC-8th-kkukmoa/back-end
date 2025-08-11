package kkukmoa.kkukmoa.voucher.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;
import lombok.Getter;

public class VoucherResponseDto {
    @Getter
    @Builder
    @Schema(description = "금액권 목록 응답 DTO")
    public static class VoucherListResponseDto {

        @Schema(description = "금액권 이름", example = "금액권 5,000원권")
        private String name;

        @Schema(description = "총 금액", example = "5000")
        private int amount;

        @Schema(description = "유효기간", example = "2026-07-29")
        private String validDays;

        @Schema(description = "금액권 상태", example = "사용중")
        private String status;

        @Schema(description = "QR 코드 UUID", example = "voucher_f8c9b7a3-xxxx-yyyy")
        private String qrCodeUuid;

        @Schema(
                description =
                        "유효기간까지 남은 일수. 만약 유효기간이 지난 경우 `-1`이 반환됩니다. 오늘 만료되면 `0`이 반환되며, 그 외에는 유효기간까지"
                            + " 남은 일수를 반환합니다.",
                example = "90")
        private int daysLeft;
    }

    @Getter
    @Builder
    @Schema(description = "금액권 상세 응답 DTO")
    public static class VoucherDetailResponseDto {

        @Schema(description = "금액권 이름", example = "금액권 5,000원권")
        private String name;

        @Schema(description = "총 금액", example = "5000")
        private int value;

        @Schema(description = "남은 금액", example = "3000")
        private int remainingValue;

        @Schema(description = "유효기간", example = "2026-07-29")
        private String validDays;

        @Schema(description = "상태", example = "사용중")
        private String status;

        @Schema(description = "QR 코드 UUID (prefix 제거된 값)", example = "f8c9b7a3-xxxx-yyyy")
        private String qrCodeUuid;

        @Schema(description = "QR 이미지 Base64", example = "iVBORw0KGgoAAAANSUhEUgAAB4AAA...")
        private String qrCode;

        @Schema(description = "유효기간까지 남은 일수", example = "90")
        private int daysLeft;
    }

    @Getter
    @Builder
    @Schema(description = "금액권 사용 차감 응답 DTO")
    public static class VoucherDeductResponseDto {

        @Schema(description = "금액권 이름", example = "금액권 5,000원권")
        private String name;

        @Schema(description = "유효기간", example = "2026-07-29")
        private String validDays;

        @Schema(description = "사용한 금액", example = "2000")
        private int usedAmount;

        @Schema(description = "남은 금액", example = "3000")
        private int remainingValue;
    }
}
