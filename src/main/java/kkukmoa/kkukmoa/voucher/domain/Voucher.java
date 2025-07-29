package kkukmoa.kkukmoa.voucher.domain;

import jakarta.persistence.*;

import kkukmoa.kkukmoa.apiPayload.exception.handler.VoucherHandler;
import kkukmoa.kkukmoa.payment.domain.Payment;
import kkukmoa.kkukmoa.stamp.enums.CouponStatus;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String voucherName;
    private Integer value; // 가격
    private Integer remainingValue; //잔액
    private String validDays; // 유효기간
    @Getter
    private CouponStatus status; // 사용 여부

    private String qrCodeUuid;

    @Lob
    private String qrImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;


    // 사용 처리 메서드
    public void markUsed() {
        this.status = CouponStatus.USED;
    }

    public void markExpired() {
        this.status = CouponStatus.EXPIRED;
    }

    public void deductValue(int amount) {
        if (remainingValue == null) {
            remainingValue = value;
        }

        if (amount <= 0) {
            throw new VoucherHandler(ErrorStatus.VOUCHER_INVALID_AMOUNT);
        }

        if (remainingValue < amount) {
            throw new VoucherHandler(ErrorStatus.VOUCHER_BALANCE_NOT_ENOUGH);
        }

        this.remainingValue -= amount;

        if (this.remainingValue == 0) {
            this.status = CouponStatus.USED;
        } else if (this.remainingValue < this.value) {
            this.status = CouponStatus.IN_USE;
        }
    }



}
