package kkukmoa.kkukmoa.voucher.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.VoucherHandler;
import kkukmoa.kkukmoa.common.util.QrCodeUtil;
import kkukmoa.kkukmoa.payment.domain.Payment;
import kkukmoa.kkukmoa.stamp.enums.CouponStatus;
import kkukmoa.kkukmoa.voucher.converter.VoucherConverter;
import kkukmoa.kkukmoa.voucher.domain.Voucher;
import kkukmoa.kkukmoa.voucher.dto.VoucherResponseDto;
import kkukmoa.kkukmoa.voucher.repository.VoucherRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.common.enums.QrCodeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

            String qrUuid = QrCodeUtil.generatePrefixedUuid(QrCodeType.VOUCHER);

            Voucher voucher = Voucher.builder()
                    .qrCodeUuid(qrUuid)
                    .voucherName(payment.getOrderName())
                    .value(unitPrice)
                    .remainingValue(unitPrice)
                    .validDays("30")
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
     * 금액권 사용 처리(아직 미완)
     */
    @Transactional
    public VoucherResponseDto.VoucherDeductResponseDto useVoucher(String qrCodeUuid, int useAmount) {
//        String stored = redisTemplate.opsForValue().get(qrCodeUuid);
//        if (stored == null) {
//            throw new VoucherHandler(ErrorStatus.QR_INVALID);
//        }

        Voucher voucher = voucherRepository.findByQrCodeUuid(qrCodeUuid)
                .orElseThrow(() -> new VoucherHandler(ErrorStatus.VOUCHER_NOT_FOUND));

        if (voucher.getStatus() == CouponStatus.USED || voucher.getStatus() == CouponStatus.EXPIRED) {
            throw new VoucherHandler(ErrorStatus.VOUCHER_ALREADY_USED);
        }


        voucher.deductValue(useAmount);
        voucherRepository.save(voucher);

//        redisTemplate.delete(qrCodeUuid); // QR 사용 후 삭제
        return VoucherConverter.toDeductDto(voucher, useAmount);

    }



}



