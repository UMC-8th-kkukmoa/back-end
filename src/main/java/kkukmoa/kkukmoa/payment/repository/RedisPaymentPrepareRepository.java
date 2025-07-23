package kkukmoa.kkukmoa.payment.repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import kkukmoa.kkukmoa.payment.dto.request.PaymentRequestDto;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
public class RedisPaymentPrepareRepository {

    private final RedisTemplate<String, PaymentRequestDto.PaymentPrepareRequestDto> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String PREFIX = "PAYMENT:";

    public RedisPaymentPrepareRepository(
            @Qualifier("paymentPrepareRedisTemplate")
                    RedisTemplate<String, PaymentRequestDto.PaymentPrepareRequestDto> redisTemplate,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void save(PaymentRequestDto.PaymentPrepareRequestDto request) {
        redisTemplate
                .opsForValue()
                .set(PREFIX + request.getOrderId(), request, Duration.ofMinutes(10));
    }

    public Optional<PaymentRequestDto.PaymentPrepareRequestDto> findByOrderId(String orderId) {
        Object raw = redisTemplate.opsForValue().get(PREFIX + orderId);
        if (raw == null) return Optional.empty();

        PaymentRequestDto.PaymentPrepareRequestDto dto =
                objectMapper.convertValue(raw, PaymentRequestDto.PaymentPrepareRequestDto.class);
        return Optional.of(dto);
    }

    public void delete(String orderId) {
        redisTemplate.delete(PREFIX + orderId);
    }
}
