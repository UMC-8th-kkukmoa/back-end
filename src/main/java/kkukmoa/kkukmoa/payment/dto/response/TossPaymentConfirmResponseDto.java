package kkukmoa.kkukmoa.payment.dto.response;

import lombok.Getter;

@Getter
public class TossPaymentConfirmResponseDto {
    private String orderId;
    private String paymentKey;
    private String orderName;
    private int totalAmount;
    private String method;
    private String approvedAt;
}
