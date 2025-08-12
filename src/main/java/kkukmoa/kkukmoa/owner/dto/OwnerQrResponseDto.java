package kkukmoa.kkukmoa.owner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import kkukmoa.kkukmoa.common.enums.QrCodeType;

import lombok.Builder;
import lombok.Getter;

public class OwnerQrResponseDto {

    @Getter
    @Builder
    public static class QrScanDto {

        @JsonProperty(value = "discount_amount")
        private Integer discountAmount;

        @JsonProperty(value = "qr_type")
        private QrCodeType qrType;
    }

    @Getter
    @Builder
    public static class QrDto {

        @JsonProperty(value = "qrcode")
        private String qrCode;
    }

    @Getter
    @Builder
    public static class QrTypeDto{

        @JsonProperty(value = "type")
        private QrCodeType type;

    }

}
