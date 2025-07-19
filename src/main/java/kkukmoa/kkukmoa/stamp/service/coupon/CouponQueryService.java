package kkukmoa.kkukmoa.stamp.service.coupon;

import java.util.List;
import kkukmoa.kkukmoa.stamp.converter.CouponConverter;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
import kkukmoa.kkukmoa.stamp.dto.couponDto.CouponResponseDto;
import kkukmoa.kkukmoa.stamp.dto.couponDto.CouponResponseDto.couponDto;
import kkukmoa.kkukmoa.stamp.repository.CouponRepository;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponQueryService {

  private final CouponRepository couponRepository;
  private final StoreRepository storeRepository;

  @Transactional(readOnly = true)
  public CouponResponseDto.couponListDto couponList(String storeType) {

    //    Store store = storeRepository.findByType(storeType);
    // 우선 하드코딩. store 부분 깃헙에 올라오면 수정
    Store store = storeRepository.findById(1L).orElse(null);
    List<Coupon> couponList = couponRepository.findByStore(store);

    // dto -> List<dto>로 변환
    List<couponDto> couponDtoList = CouponConverter.toCouponDtoList(couponList, store);

    // List<dto> -> 응답 형태로 변환 후 반환
    return CouponResponseDto.couponListDto.builder()
        .coupons(couponDtoList)
        .total(couponList.size())
        .build();
  }

}
