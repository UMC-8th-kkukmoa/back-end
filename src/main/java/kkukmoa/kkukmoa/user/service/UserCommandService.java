package kkukmoa.kkukmoa.user.service;

import jakarta.transaction.Transactional;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.UserHandler;
import kkukmoa.kkukmoa.config.security.JwtTokenProvider;
import kkukmoa.kkukmoa.user.converter.UserConverter;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.KaKaoTokenResponseDto;
import kkukmoa.kkukmoa.user.dto.KaKaoUserInfoResponseDto;
import kkukmoa.kkukmoa.user.dto.TokenResponseDto;
import kkukmoa.kkukmoa.user.dto.UserResponseDto;
import kkukmoa.kkukmoa.user.enums.SocialType;
import kkukmoa.kkukmoa.user.enums.UserType;
import kkukmoa.kkukmoa.user.repository.RefreshTokenRepository;
import kkukmoa.kkukmoa.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserCommandService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.kakao.client-id}")
    private String clientId;

    @Value("${spring.kakao.redirect-uri}")
    private String redirectUri;

    private final WebClient kakaoTokenWebClient;
    private final WebClient kakaoUserInfoWebClient;

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    private final JwtTokenProvider jwtTokenProvider;

    private final StringRedisTemplate redisTemplate;

    public UserResponseDto.loginDto loginOrRegisterByKakao(String code) {
        KaKaoTokenResponseDto token = getKakaoToken(code);
        KaKaoUserInfoResponseDto userInfo = getUserInfo(token.getAccessToken());

        String email = userInfo.getKakaoAccount().getEmail();
        String nickname =
                Optional.ofNullable(userInfo.getKakaoAccount())
                        .map(KaKaoUserInfoResponseDto.KakaoAccount::getProfile)
                        .map(KaKaoUserInfoResponseDto.KakaoAccount.Profile::getNickName)
                        .orElse("카카오사용자");

        TokenResponseDto tokenResponseDto =
                TokenResponseDto.of(token.getAccessToken(), token.getRefreshToken());

        return isnewUser(email, nickname, tokenResponseDto);
    }

    public KaKaoTokenResponseDto getKakaoToken(String code) {
        KaKaoTokenResponseDto kakaoTokenResponseDto =
                kakaoTokenWebClient
                        .post()
                        .uri("/oauth/token")
                        .body(
                                BodyInserters.fromFormData("grant_type", "authorization_code")
                                        .with("client_id", clientId)
                                        .with("redirect_uri", redirectUri)
                                        .with("code", code))
                        .retrieve()
                        .onStatus(
                                HttpStatusCode::is4xxClientError,
                                clientResponse ->
                                        Mono.error(new RuntimeException("Invalid Parameter")))
                        .onStatus(
                                HttpStatusCode::is5xxServerError,
                                clientResponse ->
                                        Mono.error(new RuntimeException("Internal Server Error")))
                        .bodyToMono(KaKaoTokenResponseDto.class)
                        .block();

        log.info("[Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
        log.info(
                "[Kakao Service] Refresh Token ------> {}",
                kakaoTokenResponseDto.getRefreshToken());
        return kakaoTokenResponseDto;
    }

    public KaKaoUserInfoResponseDto getUserInfo(String accessToken) {
        return kakaoUserInfoWebClient
                .get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KaKaoUserInfoResponseDto.class)
                .block();
    }

    /** 신규 유저인지 확인하고 가입 or 로그인 처리 */
    public UserResponseDto.loginDto isnewUser(
            String email, String nickname, TokenResponseDto tokenResponseDto) {
        return userRepository
                .findByEmail(email)
                .map(
                        user -> {
                            log.info("기존 유저 로그인: {}", user.getEmail());
                            TokenResponseDto token = jwtTokenProvider.createToken(user);
                            return userConverter.toLoginDto(user, false, token);
                        })
                .orElseGet(
                        () -> {
                            log.info("신규 유저 회원가입: {}", email);
                            User newUser = User.builder().email(email).nickname(nickname).socialType(SocialType.KAKAO)
                                    .roles(Set.of(UserType.USER)).build();

                            userRepository.save(newUser);
                            TokenResponseDto token = jwtTokenProvider.createToken(newUser);
                            return userConverter.toLoginDto(newUser, true, token);
                        });
    }

    @Transactional
    public void logout(User user, String refreshToken, String accessToken) {
        Long userIdFromRedis = refreshTokenRepository.getUserIdByToken(refreshToken);

        if (userIdFromRedis == null || !userIdFromRedis.equals(user.getId())) {
            throw new UserHandler(ErrorStatus.AUTHENTICATION_FAILED);
        }

        // Refresh Token 삭제
        refreshTokenRepository.deleteToken(refreshToken);

        // Access Token 블랙리스트 등록
        long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate
                .opsForValue()
                .set("blacklist:" + accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        log.info("로그아웃 완료 - userId: {}, Access Token 블랙리스트 등록", user.getId());
    }
}
