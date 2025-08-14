package kkukmoa.kkukmoa.user.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.TokenHandler;
import kkukmoa.kkukmoa.config.security.JwtTokenProvider;
import kkukmoa.kkukmoa.user.converter.UserConverter;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.TokenResponseDto;
import kkukmoa.kkukmoa.user.dto.UserResponseDto;
import kkukmoa.kkukmoa.user.repository.AuthExchangeRepository;
import kkukmoa.kkukmoa.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthExchangeService {

    private final AuthExchangeRepository authExchangeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final UserConverter userConverter;

    public String createAndStoreExchangeCode(UserResponseDto.loginDto loginDto) {
        TokenResponseDto tokens = loginDto.getTokenResponseDto();

        String exchangeCode = UUID.randomUUID().toString();
        authExchangeRepository.save(exchangeCode, tokens, Duration.ofSeconds(90));
        return exchangeCode;
    }

    public UserResponseDto.loginDto exchangeLogin(String code) {
        // 1. 교환 코드로 토큰 조회
        TokenResponseDto tokens = authExchangeRepository.find(code);
        if (tokens == null) {
            throw new TokenHandler(ErrorStatus.EXCHANGE_CODE_INVALID);
        }

        // 2. 코드 삭제
        authExchangeRepository.delete(code);

        // 3. subject 추출
        String subject = jwtTokenProvider.getSubjectFromToken(tokens.getAccessToken());

        User user =
                userRepository
                        .findByEmail(subject)
                        .orElseThrow(
                                () -> new TokenHandler(ErrorStatus.EXCHANGE_CODE_DESERIALIZE_FAIL));

        return userConverter.toLoginDto(user, false, tokens);
    }
}
