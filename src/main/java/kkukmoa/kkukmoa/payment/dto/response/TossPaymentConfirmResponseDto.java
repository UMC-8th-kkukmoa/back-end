package kkukmoa.kkukmoa.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "토스 결제 승인 응답 DTO")
public class TossPaymentConfirmResponseDto {

    @Schema(description = "주문 ID (결제 고유 식별자)", example = "tgen_20250729123456ABC")
    private String orderId;

    @Schema(description = "토스에서 제공하는 결제 고유 키", example = "payment_ABC123")
    private String paymentKey;

    @Schema(description = "주문 명칭 또는 상품명", example = "3만원 금액권")
    private String orderName;

    @Schema(description = "총 결제 금액 (단위: 원)", example = "30000")
    private int totalAmount;

    @Schema(description = "발급할 금액권의 단가 (단위: 원)", example = "10000")
    private int voucherUnitPrice;

    @Schema(description = "발급할 금액권 수량", example = "3")
    private int voucherQuantity;

    @Schema(description = "발급할 금액권 이름", example = "3만원 금액권")
    private String voucherName;

    @Schema(description = "결제 수단 (예: 카드, 간편결제)", example = "카드")
    private String method;

    @Schema(description = "결제 승인 일시", example = "2025-07-29T18:23:45+09:00")
    private String approvedAt;
}

