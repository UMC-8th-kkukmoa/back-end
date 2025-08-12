package kkukmoa.kkukmoa.stamp.service.coupon;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.GeneralException;
import kkukmoa.kkukmoa.apiPayload.exception.handler.CouponHandler;
import kkukmoa.kkukmoa.common.enums.QrCodeType;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.config.websocket.handler.QrWebSocketHandler;
import kkukmoa.kkukmoa.owner.dto.QrMessageDto.QrOwnerScanDto;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
import kkukmoa.kkukmoa.stamp.dto.couponDto.CouponUseResponseDto.CouponUseDto;
import kkukmoa.kkukmoa.stamp.enums.CouponStatus;
import kkukmoa.kkukmoa.stamp.repository.CouponRepository;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponCommandService {

    private final CouponRepository couponRepository;
    private final AuthService authService;
    private final QrWebSocketHandler qrWebSocketHandler;

    /**
     * 사장님이 QR 코드 스캔 후, 쿠폰일 경우 호출할 API의 메서드
     *
     * @param qrInfo QR 정보
     * @return 쿠폰 사용 후 관련 정보 응답 DTO
     */
    @Transactional
    public CouponUseDto useCoupon(String qrInfo) {

        // QR 코드
        QrCodeType qrType = QrCodeType.getQrCodeTypeByQrPrefix(qrInfo);
        // QR 코드 상태 검증
        if (qrType != QrCodeType.COUPON) {
            throw new GeneralException(ErrorStatus.QR_INVALID_TYPE);
        }

        // 쿠폰 조회
        Coupon coupon =
                couponRepository
                        .findByQrFetchUserAndStore(qrInfo) // 쿠폰 + 유저 + 가게 + 가게사장 fetch join 하여 조회
                        .orElseThrow(
                                () -> new CouponHandler(ErrorStatus.COUPON_NOT_FOUND)); // 쿠폰 조회

        // 쿠폰 소유자의 이메일 조회
        String userEmail = coupon.getUser().getEmail();

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

        // 쿠폰 상태 검증
        if (coupon.getStatus().equals(CouponStatus.USED)) {
            throw new CouponHandler(ErrorStatus.COUPON_IS_USED);
        }

        // 쿠폰 사용 처리
        coupon.use();
        log.info("쿠폰 사용 성공 : Coupon Id = {}", coupon.getId());

        // Web Socket 메시지 Dto 생성
        QrOwnerScanDto messageDto =
                QrOwnerScanDto.builder()
                        .id(coupon.getId())
                        .isSuccess(true)
                        .qrInfo(qrInfo)
                        .qrType(qrType)
                        .redirectUri(qrType.getRedirectUri())
                        .build();

        // 쿠폰 사용자에게 웹소켓으로 메시지 보냄. DTO 형태
        qrWebSocketHandler.sendMessageToEmail(userEmail, messageDto);

        return CouponUseDto.builder().couponId(coupon.getId()).qrType(qrType).build();
    }
}
