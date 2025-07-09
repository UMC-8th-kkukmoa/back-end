package kkukmoa.kkukmoa.user.service;

import kkukmoa.kkukmoa.config.security.JwtTokenProvider;
import kkukmoa.kkukmoa.user.converter.UserConverter;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.KaKaoTokenResponseDto;
import kkukmoa.kkukmoa.user.dto.KaKaoUserInfoResponseDto;
import kkukmoa.kkukmoa.user.dto.TokenResponseDto;
import kkukmoa.kkukmoa.user.dto.UserResponseDto;
import kkukmoa.kkukmoa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserCommandService {

    @Value("${spring.kakao.client-id}")
    private String clientId;

    private final WebClient kakaoTokenWebClient;
    private final WebClient kakaoUserInfoWebClient;

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    private final JwtTokenProvider jwtTokenProvider;

    public UserResponseDto.loginDto loginOrRegisterByKakao(String code) {
        KaKaoTokenResponseDto token = getKakaoToken(code);
        KaKaoUserInfoResponseDto userInfo = getUserInfo(token.getAccessToken());

        String email = userInfo.getKakaoAccount().getEmail();
        String nickname = Optional.ofNullable(userInfo.getKakaoAccount())
                .map(KaKaoUserInfoResponseDto.KakaoAccount::getProfile)
                .map(KaKaoUserInfoResponseDto.KakaoAccount.Profile::getNickName)
                .orElse("카카오사용자");


        TokenResponseDto tokenResponseDto = TokenResponseDto.of(token.getAccessToken(), token.getRefreshToken());

        return isnewUser(email, nickname, tokenResponseDto);
    }


    public KaKaoTokenResponseDto getKakaoToken(String code) {
        KaKaoTokenResponseDto kakaoTokenResponseDto = kakaoTokenWebClient
                .post()
                .uri("/oauth/token")
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("client_id", clientId)
                        .with("redirect_uri", "http://localhost:8080/users/oauth/kakao")
                        .with("code", code))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KaKaoTokenResponseDto.class)
                .block();

        log.info("[Kakao Service] Access Token ------> {}", kakaoTokenResponseDto.getAccessToken());
        log.info("[Kakao Service] Refresh Token ------> {}", kakaoTokenResponseDto.getRefreshToken());
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

    /**
     * 신규 유저인지 확인하고 가입 or 로그인 처리
     */
    public UserResponseDto.loginDto isnewUser(String email, String nickname, TokenResponseDto tokenResponseDto) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    log.info("기존 유저 로그인: {}", user.getEmail());
                    TokenResponseDto token = jwtTokenProvider.createToken(user);
                    return userConverter.toLoginDto(user, false, token);
                })
                .orElseGet(() -> {
                    log.info("신규 유저 회원가입: {}", email);
                    User newUser = User.builder()
                            .email(email)
                            .nickname(nickname)
                            .build();

                    userRepository.save(newUser);
                    TokenResponseDto token = jwtTokenProvider.createToken(newUser);
                    return userConverter.toLoginDto(newUser, true, token);
                });
    }


}
