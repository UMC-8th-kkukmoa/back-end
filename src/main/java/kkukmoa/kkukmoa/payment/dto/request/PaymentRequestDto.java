package kkukmoa.kkukmoa.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

public class PaymentRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentPrepareRequestDto implements Serializable {
        private String orderId;
        private String orderName;
        private int amount;

        private int voucherUnitPrice;
        private int voucherQuantity;

        public static PaymentPrepareRequestDto of(
                String orderId, String orderName, int amount, int unitPrice, int quantity) {
            return new PaymentPrepareRequestDto(orderId, orderName, amount, unitPrice, quantity);
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentConfirmRequestDto {
        private String paymentKey;
        private String orderId;
        private int amount;
        private int voucherUnitPrice;
        private int voucherQuantity;
    }
}
