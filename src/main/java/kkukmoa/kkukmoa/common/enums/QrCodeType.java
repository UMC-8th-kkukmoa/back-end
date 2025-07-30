package kkukmoa.kkukmoa.common.enums;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.QrHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum QrCodeType {
    VOUCHER("voucher_", "/v1/vouchers"), //
    COUPON("coupon_", "/v1/stamps/coupons"), //
    STAMP("stamp_", "/v1/stamps") //
;

    @Getter private final String qrPrefix;
    private final String redirectUri;

    public static QrCodeType getQrCodeTypeByQrPrefix(String qrPrefix) {
        return Arrays.stream(values())
                .filter(type -> type.getQrPrefix().equals(qrPrefix))
                .findFirst()
                .orElseThrow(() -> new QrHandler(ErrorStatus.QR_INVALID));
    }
}
