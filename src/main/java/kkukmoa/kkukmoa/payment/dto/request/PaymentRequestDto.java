package kkukmoa.kkukmoa.payment.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

public class PaymentRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentPrepareRequestDto implements Serializable {
        private String orderId;
        private String orderName;
        private int amount;
        public static PaymentPrepareRequestDto of(String orderId, String orderName, int amount) {
            return new PaymentPrepareRequestDto(orderId, orderName, amount);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentConfirmRequestDto {
        private String paymentKey;
        private String orderId;
        private int amount;
    }
}