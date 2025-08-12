package kkukmoa.kkukmoa.voucher.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.GeneralException;
import kkukmoa.kkukmoa.apiPayload.exception.handler.VoucherHandler;
import kkukmoa.kkukmoa.common.enums.QrCodeType;
import kkukmoa.kkukmoa.common.util.QrCodeUtil;
import kkukmoa.kkukmoa.config.websocket.handler.QrWebSocketHandler;
import kkukmoa.kkukmoa.owner.dto.QrMessageDto.QrOwnerScanDto;
import kkukmoa.kkukmoa.payment.domain.Payment;
import kkukmoa.kkukmoa.stamp.enums.CouponStatus;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.voucher.converter.VoucherConverter;
import kkukmoa.kkukmoa.voucher.domain.Voucher;
import kkukmoa.kkukmoa.voucher.dto.VoucherResponseDto;
import kkukmoa.kkukmoa.voucher.repository.VoucherRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherCommandService {

    private final VoucherRepository voucherRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final QrWebSocketHandler qrWebSocketHandler;

    /**
     * 결제 정보와 사용자 정보를 바탕으로 단가와 수량에 따라 금액권(QR 기반)을 분할 발급합니다.
     *
     * @param unitPrice 금액권 1장당 금액 (단가)
     * @param quantity 발급할 금액권 수량
     * @param user 결제한 사용자
     * @param payment 결제 엔티티
     * @return 생성된 Voucher 리스트
     */
    @Transactional
    public List<Voucher> issueVouchersByQr(
            int unitPrice, int quantity, User user, Payment payment) {
        log.info("[금액권 발급 시작] 수량: {}, 단가: {}, 사용자: {}", quantity, unitPrice, user.getEmail());

        List<Voucher> vouchers = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {

            String qrUuid = QrCodeUtil.generatePrefixedUuid(QrCodeType.VOUCHER);

            Voucher voucher =
                    Voucher.builder()
                            .qrCodeUuid(qrUuid)
                            .voucherName(payment.getOrderName())
                            .value(unitPrice)
                            .remainingValue(unitPrice)
                            .validDays(LocalDate.now().plusYears(1).toString())
                            .payment(payment)
                            .status(CouponStatus.UNUSED)
                            .user(user)
                            .build();

            vouchers.add(voucher);
        }

        List<Voucher> saved = voucherRepository.saveAll(vouchers);

        return saved;
    }

    /**
     * 사장님이 QR 코드 스캔 후, 금액권일 경우 호출할 API의 메서드
     *
     * @param qrCode QR 정보
     * @param useAmount 차감할 금액
     * @return 응답 DTO
     */
    @Transactional
    public VoucherResponseDto.VoucherDeductResponseDto useVoucher(String qrCode, int useAmount) {

        // 금액권 조회
        Voucher voucher =
                voucherRepository
                        .findByQrCodeUuid(qrCode)
                        .orElseThrow(() -> new VoucherHandler(ErrorStatus.VOUCHER_NOT_FOUND));
        log.info("조회된 금액권 ID: {}", voucher.getId());
        log.info("조회된 금액권 잔액: {}", voucher.getRemainingValue());

        // 금액 검증
        if (useAmount <= 0) { // 금액권은 정수
            throw new VoucherHandler(ErrorStatus.VOUCHER_INVALID_AMOUNT);
        } else if (useAmount > voucher.getRemainingValue()) { // 금액권 잔액 초과
            throw new VoucherHandler(ErrorStatus.VOUCHER_BALANCE_NOT_ENOUGH);
        }

        // QR 코드
        QrCodeType qrType = QrCodeType.getQrCodeTypeByQrPrefix(qrCode);
        // QR 코드 타입 검증
        if (qrType != QrCodeType.VOUCHER) {
            throw new GeneralException(ErrorStatus.QR_INVALID_TYPE);
        }

        // 금액권 상태 검증
        if (voucher.getStatus() == CouponStatus.USED
                || voucher.getStatus() == CouponStatus.EXPIRED) {
            throw new VoucherHandler(ErrorStatus.VOUCHER_ALREADY_USED);
        }

        voucher.deductValue(useAmount); // 금액권 사용
        voucherRepository.save(voucher); // 금액권의 바뀐 정보 저장

        // Web Socket 메시지 Dto 생성
        QrOwnerScanDto messageDto =
                QrOwnerScanDto.builder()
                        .id(voucher.getId())
                        .isSuccess(true)
                        .qrInfo(qrCode)
                        .qrType(qrType)
                        .redirectUri(qrType.getRedirectUri())
                        .build();

        // 쿠폰 사용자에게 웹소켓으로 메시지 보냄. DTO 형태
        String userEmail = voucher.getUser().getEmail();
        qrWebSocketHandler.sendMessageToEmail(userEmail, messageDto);

        return VoucherConverter.toDeductDto(voucher, useAmount);
    }
}
