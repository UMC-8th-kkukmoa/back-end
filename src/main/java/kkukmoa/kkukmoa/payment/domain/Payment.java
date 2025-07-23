package kkukmoa.kkukmoa.payment.domain;

import jakarta.persistence.*;
import kkukmoa.kkukmoa.apiPayload.exception.PaymentHandler;
import kkukmoa.kkukmoa.common.BaseEntity;
import kkukmoa.kkukmoa.payment.dto.response.TossPaymentConfirmResponseDto;
import kkukmoa.kkukmoa.user.domain.User;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 200)
    private String orderId;             // 주문 번호

    @Column(nullable = false, length = 200)
    private String paymentKey;          // Toss 결제 키

    @Column(nullable = false)
    private String orderName;           // 금액권 이름

    @Column(nullable = false)
    private int amount;                 // 결제 금액

    private String method;              // 카드, 계좌이체, 휴대폰 등

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;       // PENDING, SUCCESS, FAIL

    private LocalDateTime approvedAt;   // 승인 시각

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateFromTossResponse(TossPaymentConfirmResponseDto res) {
        this.paymentKey = res.getPaymentKey();
        this.method = res.getMethod();
        this.amount = res.getTotalAmount();
        this.approvedAt = LocalDateTime.parse(res.getApprovedAt());
        this.status = PaymentStatus.SUCCESS;
    }


}

