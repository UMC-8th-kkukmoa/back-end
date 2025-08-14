package kkukmoa.kkukmoa.user.service;

import jakarta.transaction.Transactional;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.UserHandler;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.config.security.JwtTokenProvider;
import kkukmoa.kkukmoa.user.converter.UserConverter;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserCommandService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthService authService;

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

    private final PasswordEncoder passwordEncoder;

    public UserResponseDto.loginDto loginOrRegisterByKakao(String code) {
        KaKaoTokenResponseDto token = getKakaoToken(code);
        KaKaoUserInfoResponseDto userInfo = getUserInfo(token.getAccessToken());

        String email = userInfo.getKakaoAccount().getEmail();
        String nickname =
                Optional.ofNullable(userInfo.getKakaoAccount())
                        .map(KaKaoUserInfoResponseDto.KakaoAccount::getProfile)
                        .map(KaKaoUserInfoResponseDto.KakaoAccount.Profile::getNickName)
                        .orElse("카카오사용자");

        return isnewUser(email, nickname);
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
    public UserResponseDto.loginDto isnewUser(String email, String nickname) {
        return userRepository
                .findByEmail(email)
                .map(
                        user -> {
                            log.info("기존 유저 로그인: {}", user.getEmail());

                            // 기본 role이 비어 있으면 USER 추가
                            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                                user.addRole(UserType.USER);
                                userRepository.save(user);
                            }

                            TokenResponseDto token = jwtTokenProvider.createToken(user);
                            return userConverter.toLoginDto(user, false, token);
                        })
                .orElseGet(
                        () -> {
                            log.info("신규 유저 회원가입: {}", email);

                            String uniqueNickname = nickname + getRandomNumber(4);

                            User newUser =
                                    User.builder()
                                            .email(email)
                                            .nickname(uniqueNickname)
                                            .socialType(SocialType.KAKAO)
                                            .agreeTerms(false)
                                            .agreePrivacy(false)
                                            .roles(Set.of(UserType.USER))
                                            .build();

                            userRepository.save(newUser);

                            TokenResponseDto token = jwtTokenProvider.createToken(newUser);
                            return userConverter.toLoginDto(newUser, true, token);
                        });
    }

    private String getRandomNumber(int length) {
        int max = (int) Math.pow(10, length);
        int number = new Random().nextInt(max);
        return String.format("%0" + length + "d", number);
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

    @Transactional
    public void registerLocalUser(LocalSignupRequestDto request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserHandler(ErrorStatus.DUPLICATION_DUPLICATION_EMAIL);
        }

        // 닉네임 중복 체크
        String nick = request.getNickname();
        if (nick == null || nick.trim().isEmpty()) {
            throw new UserHandler(ErrorStatus.INVALID_PARAMETER);
        }
        if (userRepository.existsByNicknameIgnoreCase(nick.trim())) {
            throw new UserHandler(ErrorStatus.DUPLICATION_NICKNAME); // 전용 코드
        }

        // 사용자 생성 (시연용: 최소 필드만)
        User user =
                User.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword())) // 비밀번호는 반드시 해시
                        .nickname(request.getNickname())
                        .birthday(request.getBirthday())
                        .socialType(SocialType.LOCAL) // 로컬 가입 표시
                        .roles(Set.of(UserType.USER)) // 일반 유저 역할
                        .build();

        userRepository.save(user);
    }

    @Transactional
    public TokenResponseDto loginLocalUser(LocalLoginRequestDto request) {
        User user =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenProvider.createToken(user);
    }

    @Transactional
    public String createUserUuid() {
        // 현재 로그인한 사용자 가져오기
        User user = authService.getCurrentUser();

        // UUID가 없으면 새로 생성
        if (user.getUuid() == null || user.getUuid().isEmpty()) {
            String newUuid = UUID.randomUUID().toString();
            user.setUuid(newUuid);

            // 트랜잭션 내에서 엔티티 변경 사항 반영
            userRepository.save(user); // 명시적으로 save() 호출

            return newUuid;
        }

        // 이미 있으면 그대로 반환
        return user.getUuid();
    }
}
