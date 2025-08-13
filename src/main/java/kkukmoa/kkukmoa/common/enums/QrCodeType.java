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

    public static QrCodeType getQrCodeTypeByQrPrefix(String qrCode) {

        // QR 정보에서 QR 유형 정보 추출
        int cutIndex = qrCode.indexOf("_");
        String prefix = qrCode.substring(0, cutIndex + 1); // ex) "voucher_" , "coupon_", "stamp_"

        // 찾아서 반환
        return Arrays.stream(values())
                .filter(type -> type.getQrPrefix().equals(prefix))
                .findFirst()
                .orElseThrow(() -> new QrHandler(ErrorStatus.QR_INVALID));
    }
}
