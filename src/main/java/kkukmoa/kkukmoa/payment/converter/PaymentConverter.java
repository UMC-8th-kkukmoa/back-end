package kkukmoa.kkukmoa.payment.converter;

import kkukmoa.kkukmoa.payment.domain.Payment;
import kkukmoa.kkukmoa.payment.domain.PaymentStatus;
import kkukmoa.kkukmoa.payment.dto.request.PaymentRequestDto;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentConverter {
    public static Payment toEntity(
            PaymentRequestDto.PaymentPrepareRequestDto requestDto, User user) {
        return Payment.builder()
                .orderId(requestDto.getOrderId())
                .orderName(requestDto.getOrderName())
                .amount(requestDto.getAmount())
                .status(PaymentStatus.PENDING)
                .user(user)
                .build();
    }
    public static PaymentRequestDto.PaymentConfirmRequestDto toConfirmDto(
            String paymentKey,
            String orderId,
            int amount,
            Integer unitPrice,
            Integer quantity
    ) {
        return PaymentRequestDto.PaymentConfirmRequestDto.builder()
                .paymentKey(paymentKey)
                .orderId(orderId)
                .amount(amount)
                .voucherUnitPrice(unitPrice)
                .voucherQuantity(quantity)
                .build();
    }

}
