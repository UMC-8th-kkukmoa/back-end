package kkukmoa.kkukmoa.owner.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.CouponHandler;
import kkukmoa.kkukmoa.apiPayload.exception.handler.QrHandler;
import kkukmoa.kkukmoa.common.enums.QrCodeType;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.config.websocket.handler.QrWebSocketHandler;
import kkukmoa.kkukmoa.owner.dto.OwnerQrResponseDto;
import kkukmoa.kkukmoa.owner.dto.QrMessageDto.QrOwnerScanDto;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
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
public class OwnerCommandService {

    private final AuthService authService;
    private final QrWebSocketHandler qrWebSocketHandler;
    private final CouponRepository couponRepository;

    // 사장님이 QR 스캔 메서드
    @Transactional(readOnly = false)
    public OwnerQrResponseDto.QrScanDto scanQrCode(String qrInfo) {

        // QR 정보에서 QR 유형 정보 추출
        int cutIndex = qrInfo.indexOf("_");
        String prefix = qrInfo.substring(0, cutIndex + 1); // ex) "voucher_" , "coupon_", "stamp_"
        QrCodeType qrType = QrCodeType.getQrCodeTypeByQrPrefix(prefix);

        // 쿠폰&금액권 사용 성공 여부
        int discountAmount = 0;
        String userEmail = "";

        // 쿠폰 종류에 따른 가격 차감 처리
        if (qrType == QrCodeType.COUPON) { // 쿠폰

            // 필요한 정보 조회
            Coupon coupon =
                    couponRepository
                            .findByQrFetchUserAndStore(
                                    qrInfo) // 쿠폰 + 유저 + 가게 + 가게사장 fetch join 하여 조회
                            .orElseThrow(
                                    () -> new CouponHandler(ErrorStatus.COUPON_NOT_FOUND)); // 쿠폰 조회
            userEmail += coupon.getUser().getEmail(); // 쿠폰 소유자의 이메일 조회

            // 동작
            discountAmount = coupon.getDiscountAmount(); // 차감 금액 설정
            useCoupon(coupon); // 쿠폰 사용

        } else if (qrType == QrCodeType.VOUCHER) { // 금액권

            // TODO: 금액권 로직 작성하면 됩니다. 더 좋은 로직 있으면 공유좀...

        } else { // 스탬프
            throw new QrHandler(ErrorStatus.OWNER_INVALID_SCAN); // 사장은 스탬프 QR 스캔할 수 없음
        }

        // Web Socket 메시지 Dto 생성
        QrOwnerScanDto messageDto =
                QrOwnerScanDto.builder()
                        .isSuccess(true)
                        .qrInfo(qrInfo)
                        .qrType(qrType)
                        .discountAmount(discountAmount)
                        .redirectUri(qrType.getRedirectUri())
                        .build();

        // 쿠폰 사용자에게 웹소켓으로 메시지 보냄. DTO 형태
        qrWebSocketHandler.sendMessageToEmail(userEmail, messageDto);

        return OwnerQrResponseDto.QrScanDto.builder()
                .discountAmount(discountAmount)
                .qrType(qrType)
                .build();
    }
    /**
     *  사장이 사용자의 쿠폰 QR 코드 인식 후의 처리입니다. 사장검증 -> 쿠폰 유효성 검사( 사용여부 ) -> 쿠폰 사용 처리 ( 상태 변경, 금액 차감 )
     *
     * @param 'coupon_uuid' 형태의 QR 정보
     * @return 쿠폰 사용 성공 여부 반환
     */
    private void useCoupon(Coupon coupon) {

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
