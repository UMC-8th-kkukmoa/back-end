package kkukmoa.kkukmoa.stamp.service.coupon;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.GeneralException;
import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.category.repository.CategoryRepository;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.stamp.converter.CouponConverter;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
import kkukmoa.kkukmoa.stamp.dto.couponDto.CouponResponseDto;
import kkukmoa.kkukmoa.stamp.dto.couponDto.CouponResponseDto.couponDto;
import kkukmoa.kkukmoa.stamp.repository.CouponRepository;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponQueryService {

    private final CategoryRepository categoryRepository;
    private final CouponRepository couponRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public CouponResponseDto.couponListDto couponList(CategoryType storeType) {

        // 로그인한 유저
        User user = authService.getCurrentUser();

        // List<Coupon> 생성
        List<Coupon> couponList;

        // 쿠폰 조회
        if (storeType != null) { // store 카테고리 조건 있음

            Category category =
                    categoryRepository
                            .findByType(storeType)
                            .orElseThrow(
                                    () ->
                                            new GeneralException(
                                                    ErrorStatus.STORE_CATEGORY_NOT_FOUND));

            // category, user로 조회
            couponList = couponRepository.findByCategoryAndUser(category, user);

        } else { // store 카테고리 없음 ( ALL )
            // user로 조회
            couponList = couponRepository.findByUser(user);
        }

        // dto -> List<dto>로 변환
        List<couponDto> couponDtoList = CouponConverter.toCouponDtoList(couponList);

        // List<dto> -> 응답 형태로 변환 후 반환
        return CouponResponseDto.couponListDto
                .builder()
                .coupons(couponDtoList)
                .total(couponList.size())
                .build();
    }
}
