package kkukmoa.kkukmoa.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QrCodeType {

  VOUCHER("voucher_", "/v1/vouchers/"), //
  COUPON("coupon_", "/v1/stamps/coupons/"), //
  STAMP("stamp_", "/v1/stamps/") //
  ;

  private final String qrPrefix;
  private final String uri; // 각 URI 뒤에 금액권 & 쿠폰 & 스탬프의 ID 넣도록...

}