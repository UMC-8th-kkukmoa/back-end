package kkukmoa.kkukmoa.owner.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.UserHandler;
import kkukmoa.kkukmoa.config.security.JwtTokenProvider;
import kkukmoa.kkukmoa.owner.dto.OwnerSignupRequest;
import kkukmoa.kkukmoa.owner.dto.OwnerLoginRequest;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.TokenResponseDto;
import kkukmoa.kkukmoa.user.enums.SocialType;
import kkukmoa.kkukmoa.user.enums.UserType;
import kkukmoa.kkukmoa.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class OwnerAccountService {

    // 사장님 회원가입 서비스

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void registerLocalOwner(OwnerSignupRequest request) {
        // 0. 약관 동의 여부 확인
        if (!request.isAgreeTerms()) {
            throw new UserHandler(ErrorStatus.TERMS_NOT_AGREED);
        }
        if (!request.isAgreePrivacy()) {
            throw new UserHandler(ErrorStatus.PRIVACY_NOT_AGREED);
        }

        // 1. 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserHandler(ErrorStatus.DUPLICATION_DUPLICATION_EMAIL);
        }

        // 2. 유저 생성
        User user =
                User.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .socialType(SocialType.LOCAL)
                        .agreeTerms(request.isAgreeTerms())
                        .agreePrivacy(request.isAgreePrivacy())
                        .roles(Set.of(UserType.PENDING_OWNER)) // owner 승인 대기 role 부여
                        .build();

        userRepository.save(user);
    }

    @Transactional
    public TokenResponseDto loginOwner(OwnerLoginRequest request) {
        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserHandler(ErrorStatus.PASSWORD_NOT_MATCH);
        }

        return jwtTokenProvider.createToken(user); // access + refresh token 발급 및 저장
    }
}
