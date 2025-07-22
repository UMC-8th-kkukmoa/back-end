package kkukmoa.kkukmoa.owner.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.CouponHandler;
import kkukmoa.kkukmoa.apiPayload.exception.handler.QrHandler;
import kkukmoa.kkukmoa.common.enums.QrCodeType;
import kkukmoa.kkukmoa.config.websocket.handler.QrWebSocketHandler;
import kkukmoa.kkukmoa.owner.dto.OwnerQrResponseDto;
import kkukmoa.kkukmoa.owner.dto.QrMessageDto.QrOwnerScanDto;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
import kkukmoa.kkukmoa.stamp.repository.CouponRepository;
import kkukmoa.kkukmoa.stamp.service.coupon.CouponCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerCommandService {

  private final CouponCommandService couponCommandService;
  private final QrWebSocketHandler  qrWebSocketHandler;
  private final CouponRepository couponRepository;

  // 사장님이 QR 스캔 메서드
  @Transactional(readOnly = false)
  public OwnerQrResponseDto.QrScanDto scanQrCode(String qrInfo) {


    // QR 정보에서 QR 유형 정보 추출
    int cutIndex = qrInfo.indexOf("_");
    String prefix = qrInfo.substring(0, cutIndex+1); // ex) "voucher_" , "coupon_", "stamp_"
    QrCodeType qrType = QrCodeType.getQrCodeTypeByQrPrefix(prefix);


    // 쿠폰&금액권 사용 성공 여부
    int discountAmount = 0;
    String userEmail = "";


    // 쿠폰 종류에 따른 가격 차감 처리
    if(qrType == QrCodeType.COUPON) { // 쿠폰

      // 필요한 정보 조회
      Coupon coupon = couponRepository.findByQrFetchUserAndStore(qrInfo) // 쿠폰 + 유저 + 가게 + 가게사장 fetch join 하여 조회
          .orElseThrow(() -> new CouponHandler(ErrorStatus.COUPON_NOT_FOUND)); // 쿠폰 조회
      userEmail += coupon.getUser().getEmail(); // 쿠폰 소유자의 이메일 조회

      // 동작
      discountAmount = coupon.getDiscountAmount(); // 차감 금액 설정
      couponCommandService.useCoupon(coupon); // 쿠폰 사용

    }else if (qrType == QrCodeType.VOUCHER) { // 금액권

      // TODO: 금액권 로직 작성하면 됩니다. 더 좋은 로직 있으면 공유좀...

    }else { // 스탬프
      throw new QrHandler(ErrorStatus.OWNER_INVALID_SCAN); // 사장은 스탬프 QR 스캔할 수 없음
    }

    // Web Socket 메시지 Dto 생성
    QrOwnerScanDto messageDto = QrOwnerScanDto.builder()
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
}
