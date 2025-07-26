package kkukmoa.kkukmoa.stamp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CouponName {

  SERVICE("서비스 쿠폰"),
  REWARD("리워드 쿠폰")
  ;

  private final String name;

}
