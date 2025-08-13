package kkukmoa.kkukmoa.voucher.domain;

import jakarta.persistence.*;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.VoucherHandler;
import kkukmoa.kkukmoa.common.BaseEntity;
import kkukmoa.kkukmoa.payment.domain.Payment;
import kkukmoa.kkukmoa.stamp.enums.CouponStatus;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String voucherName;
    private Integer value; // 가격
    private Integer remainingValue; // 잔액
    private String validDays; // 유효기간

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CouponStatus status = CouponStatus.UNUSED;

    private String qrCodeUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    public void deductValue(int amount) {
        // 값 없으면 기본값 설정
        if (remainingValue == null) {
            remainingValue = value;
        }

        // 수량 없으면 예외 발생
        if (amount <= 0) {
            throw new VoucherHandler(ErrorStatus.VOUCHER_INVALID_AMOUNT);
        }

        // 잔액 부족 시 예외 발생
        if (remainingValue < amount) {
            throw new VoucherHandler(ErrorStatus.VOUCHER_BALANCE_NOT_ENOUGH);
        }

        // 금액 차감
        this.remainingValue -= amount;

        // 금액권 상태 변경
        if (this.remainingValue == 0) { // 금액 소진 시 상태 'USED' 로 상태 변경
            this.status = CouponStatus.USED;
        } else if (this.remainingValue < this.value) { // 'IN_USE' 로 상태 변경
            this.status = CouponStatus.IN_USE;
        }
    }
}
