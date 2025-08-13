package kkukmoa.kkukmoa.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@RequiredArgsConstructor
@Service
public class RedisUtil {
    private final StringRedisTemplate template;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public void setWithTtl(String key, String value, Duration ttl) {
        template.opsForValue().set(key, value, ttl);
    }

    public String get(String key) {
        return template.opsForValue().get(key);
    }

    public void delete(String key) {
        template.delete(key);
    }

    public boolean exists(String key) {
        Boolean b = template.hasKey(key);
        return Boolean.TRUE.equals(b);
    }

    private String otpKey(String email) {
        return "otp:email:" + mustNormalize(email);
    }

    private String triesKey(String email) {
        return "otp:tries:" + mustNormalize(email);
    }

    private String rateKey(String email) {
        return "otp:rate:" + mustNormalize(email);
    }

    public String generateNumericOtp() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    /**
     * 기존 OTP가 살아있으면 새로 쓰지 않음(NX). true=저장 성공, false=기존 있음
     */
    public boolean saveEmailOtpNx(String email, String code, Duration ttl) {
        Boolean ok = template.opsForValue().setIfAbsent(otpKey(email), code, ttl);
        return Boolean.TRUE.equals(ok);
    }

    public void saveEmailOtpOverwrite(String email, String code, Duration ttl) {
        setWithTtl(otpKey(email), code, ttl);
    }

    public String getEmailOtp(String email) {
        return get(otpKey(email));
    }

    public void deleteEmailOtp(String email) {
        delete(otpKey(email));
    }

    public boolean acquireSendCooldown(String email, Duration cooldown) {
        Boolean ok = template.opsForValue().setIfAbsent(rateKey(email), "1", cooldown);
        return Boolean.TRUE.equals(ok);
    }

    public long incrementTries(String email, Duration ttl) {
        Long v = template.opsForValue().increment(triesKey(email));
        template.expire(triesKey(email), ttl);
        return v == null ? 0 : v;
    }
    public void clearTries(String email) {
        delete(triesKey(email));
    }

    private String mustNormalize(String email) {
        if (email == null) throw new IllegalArgumentException("email is null");
        String e = email.trim().toLowerCase();
        if (e.isEmpty()) throw new IllegalArgumentException("email is blank");
        return e;
    }
}