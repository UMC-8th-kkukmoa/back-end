package kkukmoa.kkukmoa.stamp.service.coupon;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.CouponHandler;
import kkukmoa.kkukmoa.common.enums.QrCodeType;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
import kkukmoa.kkukmoa.stamp.enums.CouponStatus;
import kkukmoa.kkukmoa.stamp.repository.CouponRepository;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponCommandService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    private final AuthService authService;

    @Transactional(readOnly = false)
    public Coupon saveCoupon() {

        String qrInfo = QrCodeType.COUPON.getQrPrefix() + UUID.randomUUID();
        User user = userRepository.findById(2L).orElse(null);
        Store store = storeRepository.findById(1L).orElse(null);

        Coupon coupon =
                Coupon.builder()
                        .name("name1")
                        .description("description1")
                        .discountAmount(1)
                        .status(CouponStatus.UNUSED)
                        //        .qrImage(QrCodeUtil.makeQrCode(qrInfo))
                        .qrCode(qrInfo)
                        .user(user)
                        .store(store)
                        .build();

        return couponRepository.save(coupon);
    }

    /**
     * 사장이 사용자의 쿠폰 QR 코드 인식 후의 처리입니다. 사장검증 -> 쿠폰 유효성 검사( 사용여부 ) -> 쿠폰 사용 처리 ( 상태 변경, 금액 차감 )
     *
     * @param 'coupon_uuid' 형태의 QR 정보
     * @return 쿠폰 사용 성공 여부 반환
     */
    public void useCoupon(Coupon coupon) {

        // 쿠폰이 속한 가게의 사장
        User couponOwner = coupon.getStore().getOwner();
        log.info("조회된 쿠폰 ID : {}", coupon.getId());
        log.info("조회된 쿠폰 상태 : {}", coupon.getStatus());
        log.info("쿠폰 가게 사장님 정보 : {}", couponOwner.getEmail());

        // 사장 검증
        Long currentLoginOwnerId = authService.getCurrentUserId();
        log.info("현재 로그인된 사장님 정보 : {}", authService.getCurrentUserEmail());

        // 사장 ID 비교
        if (!couponOwner.getId().equals(currentLoginOwnerId)) {
            throw new CouponHandler(ErrorStatus.COUPON_INVALID_USED_PLACE);
        }

        // 쿠폰 유효성 검사
        if (coupon.getStatus().equals(CouponStatus.USED)) {
            throw new CouponHandler(ErrorStatus.COUPON_IS_USED);
        }

        // 쿠폰 사용 처리
        coupon.use();
        log.info("쿠폰 사용 성공 : Coupon Id = {}", coupon.getId());
    }
}
