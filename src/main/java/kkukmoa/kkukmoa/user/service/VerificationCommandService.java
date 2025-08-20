package kkukmoa.kkukmoa.user.service;

import kkukmoa.kkukmoa.common.util.RedisUtil;
import kkukmoa.kkukmoa.config.security.JwtTokenProvider;
import kkukmoa.kkukmoa.user.dto.VerificationConfirmResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VerificationCommandService {

    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final Duration OTP_RATE = Duration.ofSeconds(60);
    private static final Duration TRIES_TTL = Duration.ofMinutes(10);
    private static final int MAX_TRIES = 5;
    private static final Duration SIGNUP_TOKEN_TTL = Duration.ofMinutes(15);

    private final RedisUtil redis;
    private final MailService mail;
    private final JwtTokenProvider jwtTokenProvider;

    public void requestOtp(String email) {
        if (!redis.acquireSendCooldown(email, OTP_RATE)) return;
        String code = redis.generateNumericOtp();
        boolean created = redis.saveEmailOtpNx(email, code, OTP_TTL);
        if (!created) return;

        mail.sendOtp(email, code);
    }

    public VerificationConfirmResponseDto confirm(String email, String inputCode) {
        String saved = redis.getEmailOtp(email);
        if (saved == null) throw new IllegalArgumentException("인증번호가 만료되었습니다.");

        if (!saved.equals(inputCode)) {
            long tries = redis.incrementTries(email, TRIES_TTL);
            if (tries >= MAX_TRIES) {
                redis.deleteEmailOtp(email);
                redis.clearTries(email);
            }
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        redis.deleteEmailOtp(email);
        redis.clearTries(email);

        String jti = UUID.randomUUID().toString();
        String signupToken = jwtTokenProvider.issueSignupToken(email, jti, SIGNUP_TOKEN_TTL);
        return new VerificationConfirmResponseDto(signupToken, SIGNUP_TOKEN_TTL.toSeconds());
    }
}
