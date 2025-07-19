package kkukmoa.kkukmoa.stamp.converter;

import java.util.List;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
import kkukmoa.kkukmoa.stamp.dto.couponDto.CouponResponseDto;
import kkukmoa.kkukmoa.store.domain.Store;

public class CouponConverter {

  public static CouponResponseDto.couponDto toCouponDto(Coupon coupon, Store store) {
    return CouponResponseDto.couponDto.builder()
        .couponId(coupon.getId())
        .storeId(store.getId())
        .storeImg("imgURL")
        .storeName("storeName")
        .storeType("storeType")
        .couponName(coupon.getName())
        .build();
  }

  public static List<CouponResponseDto.couponDto> toCouponDtoList(List<Coupon> coupons, Store store) {
    return coupons.stream().map(
        coupon -> CouponConverter.toCouponDto(coupon, store)
    ).toList();
  }

}
