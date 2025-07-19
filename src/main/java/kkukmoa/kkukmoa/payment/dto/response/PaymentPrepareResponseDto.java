package kkukmoa.kkukmoa.payment.dto.response;

import java.io.Serializable;

public record PaymentPrepareResponseDto(String orderId, String orderName, int amount) implements Serializable {}
