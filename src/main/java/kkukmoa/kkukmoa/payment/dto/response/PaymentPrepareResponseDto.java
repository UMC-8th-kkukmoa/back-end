package kkukmoa.kkukmoa.payment.dto;

import java.io.Serializable;

public record PaymentPrepareResponseDto(String orderId, String orderName, int amount) implements Serializable {}
