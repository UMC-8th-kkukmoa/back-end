package kkukmoa.kkukmoa.owner.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import kkukmoa.kkukmoa.common.enums.QrCodeType;

import lombok.Builder;
import lombok.Getter;

/** QR 코드 스캔 후 웹소캣으로 보낼 메시지의 Dto 입니다. */
public class QrMessageDto {

    @Getter
    @Builder // 문자열만 보낼 때의 Dto
    public static class QrGeneralTextDto extends QrMessageDto {

        @JsonProperty(value = "message")
        private String message;
    }

    @Getter
    @Builder // 사장이 QR 코드를 찍었을 때 웹소켓으로 메시지 보내는 Dto
    public static class QrOwnerScanDto extends QrMessageDto {

        @JsonProperty(value = "id")
        private Long id;

        @JsonProperty(value = "is_success")
        private Boolean isSuccess;

        @JsonProperty(value = "qr_info")
        private String qrInfo;

        @JsonProperty(value = "qr_type")
        private QrCodeType qrType;

        @JsonProperty(value = "redirect_uri")
        private String redirectUri;
    }

    @Getter
    @Builder
    public static class StampScanDto extends QrMessageDto {
        // TODO: 스탬프 적립 후 웹소켓으로 넘길 정보 적기
    }
}
