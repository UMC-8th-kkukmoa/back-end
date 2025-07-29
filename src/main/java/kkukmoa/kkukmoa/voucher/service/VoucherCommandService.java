package kkukmoa.kkukmoa.voucher.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.VoucherHandler;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.common.util.QrCodeUtil;
import kkukmoa.kkukmoa.payment.domain.Payment;
import kkukmoa.kkukmoa.payment.dto.response.TossPaymentConfirmResponseDto;
import kkukmoa.kkukmoa.stamp.enums.CouponStatus;
import kkukmoa.kkukmoa.voucher.converter.VoucherConverter;
import kkukmoa.kkukmoa.voucher.domain.Voucher;
import kkukmoa.kkukmoa.voucher.repository.VoucherRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.repository.UserRepository;
import kkukmoa.kkukmoa.common.enums.QrCodeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherCommandService {

    private final VoucherRepository voucherRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String QR_PREFIX = QrCodeType.VOUCHER.getQrPrefix();
    private final QrCodeUtil qrCodeUtil;
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
    public List<Voucher> issueVouchersByQr(int unitPrice, int quantity, User user, Payment payment) {
        log.info("[금액권 발급 시작] 수량: {}, 단가: {}, 사용자: {}", quantity, unitPrice, user.getEmail());

        List<Voucher> vouchers = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            String uuid = UUID.randomUUID().toString();
            String qrInfo = QrCodeType.VOUCHER.getQrPrefix() + uuid;

            Voucher voucher = Voucher.builder()
                    .qrCodeUuid("voucher_" + uuid)
                    .voucherName(payment.getOrderName())
                    .value(unitPrice)
                    .remainingValue(unitPrice)
                    .validDays("30")
                    .payment(payment)
                    .status(CouponStatus.UNUSED)
                    .qrImage(QrCodeUtil.qrCodeToBase64(qrInfo))
                    .user(user)
                    .build();

            log.info("[금액권 생성] [{}] {}", i + 1, voucher.getQrCodeUuid());
            vouchers.add(voucher);
        }

        List<Voucher> saved = voucherRepository.saveAll(vouchers);
        log.info("[금액권 저장 완료] 총 {}건", saved.size());

        return saved;
    }

    /**
     * 금액권 사용 처리
     */
    @Transactional
    public void useVoucher(String qrCodeUuid, int useAmount) {
        String stored = redisTemplate.opsForValue().get(qrCodeUuid);
        if (stored == null) {
            throw new VoucherHandler(ErrorStatus.QR_EXPIRED);
        }

        Voucher voucher = voucherRepository.findByQrCodeUuid(qrCodeUuid)
                .orElseThrow(() -> new VoucherHandler(ErrorStatus.VOUCHER_NOT_FOUND));

        if (voucher.getStatus() == CouponStatus.USED) {
            throw new VoucherHandler(ErrorStatus.VOUCHER_ALREADY_USED);
        }

        voucher.deductValue(useAmount);
        voucherRepository.save(voucher);

        redisTemplate.delete(qrCodeUuid); // QR 사용 후 삭제
    }

    @Transactional
    public void deductTest(String qrCodeUuid, int amount) {
        Voucher voucher = voucherRepository.findByQrCodeUuid(qrCodeUuid)
                .orElseThrow(() -> new VoucherHandler(ErrorStatus.VOUCHER_NOT_FOUND));

        voucher.deductValue(amount);
        voucherRepository.save(voucher);
    }

}



