package kkukmoa.kkukmoa.stamp.converter;

import kkukmoa.kkukmoa.common.util.QrCodeUtil;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
import kkukmoa.kkukmoa.stamp.dto.couponDto.CouponResponseDto;
import kkukmoa.kkukmoa.store.domain.Store;

import java.util.List;

public class CouponConverter {

    public static CouponResponseDto.couponDto toCouponDto(Coupon coupon, Store store) {
        return CouponResponseDto.couponDto
                .builder()
                .couponId(coupon.getId())
                .storeId(store.getId())
                .storeImg("imgURL")
                .storeName("storeName")
                .storeType("storeType")
                .couponName(coupon.getName())
                .couponQrCode(QrCodeUtil.qrCodeToBase64(coupon.getQrCode()))
                .build();
    }

    public static List<CouponResponseDto.couponDto> toCouponDtoList(
            List<Coupon> coupons, Store store) {
        return coupons.stream().map(coupon -> CouponConverter.toCouponDto(coupon, store)).toList();
    }
}
