package kkukmoa.kkukmoa.payment.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public class PaymentRequestDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentPrepareRequestDto implements Serializable {
        private String orderId;
        private String orderName;
        private int amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentConfirmRequestDto {
        private String paymentKey;
        private String orderId;
        private int amount;
    }
}