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
    public CouponResponseDto.couponListDto couponList(CategoryType requestCategory) {

        // 로그인한 유저
        User user = authService.getCurrentUser();

        // 요청 받은 카테고리 예외 처리
        Category category =
                categoryRepository
                        .findByType(requestCategory)
                        .orElseThrow(
                                () -> new GeneralException(ErrorStatus.STORE_CATEGORY_NOT_FOUND));

        // 쿠폰 조회
        List<Coupon> couponList = couponRepository.findByCategoryAndUser(category, user);

        List<couponDto> couponDtoList = CouponConverter.toCouponDtoList(couponList);

        return CouponResponseDto.couponListDto
                .builder()
                .coupons(couponDtoList)
                .total(couponList.size())
                .build();
    }
}
