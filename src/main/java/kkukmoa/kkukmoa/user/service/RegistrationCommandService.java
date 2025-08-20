package kkukmoa.kkukmoa.user.service;

import kkukmoa.kkukmoa.config.security.JwtTokenProvider;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.SignupRequestDto;
import kkukmoa.kkukmoa.user.enums.SocialType;
import kkukmoa.kkukmoa.user.enums.UserType;
import kkukmoa.kkukmoa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistrationCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void signup(SignupRequestDto req) {
        final String email = normalize(req.email());

        final SocialType provider = SocialType.LOCAL;

        final UserType role = req.role();
        if (role == null) {
            throw new IllegalArgumentException("role 값이 없습니다.");
        }

        if (provider == SocialType.LOCAL && role == UserType.USER) {
            validateSignupToken(req.signupToken(), email);
        }

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        User user =
                User.builder()
                        .email(email)
                        .password(encoder.encode(req.password()))
                        .nickname(req.nickname())
                        .socialType(provider)
                        .build();

        user.addRole(UserType.USER);

        userRepository.save(user);
    }

    private void validateSignupToken(String token, String email) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("이메일 인증이 필요합니다.");
        }
        Map<String, Object> claims = jwtTokenProvider.parseClaims(token);
        if (!"signup".equals(claims.get("purpose"))) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        String tokenEmail = (String) claims.get("sub");
        if (!email.equalsIgnoreCase(tokenEmail)) {
            throw new IllegalArgumentException("인증된 이메일과 일치하지 않습니다.");
        }
    }

    private String normalize(String email) {
        if (email == null) return null;
        String e = email.trim().toLowerCase();
        if (e.isEmpty()) throw new IllegalArgumentException("이메일이 비어 있습니다.");
        return e;
    }
}
