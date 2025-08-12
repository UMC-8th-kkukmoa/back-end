package kkukmoa.kkukmoa.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import kkukmoa.kkukmoa.apiPayload.code.ErrorReasonDto;
import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.common.util.swagger.ApiErrorCodeExamples;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.LocalLoginRequest;
import kkukmoa.kkukmoa.user.dto.LocalSignupRequest;
import kkukmoa.kkukmoa.user.dto.TokenResponseDto;
import kkukmoa.kkukmoa.user.dto.UserResponseDto;
import kkukmoa.kkukmoa.user.repository.AuthExchangeRepository;
import kkukmoa.kkukmoa.user.service.ReissueService;
import kkukmoa.kkukmoa.user.service.UserCommandService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@Tag(name = "사용자 API", description = "사용자 관련 API 입니다.")
public class UserController {

    private final UserCommandService userCommandService;
    private final AuthExchangeRepository authExchangeRepository;
    private final ReissueService reissueService;

    @GetMapping("/oauth/kakao")
    @Operation(
            summary = "카카오 로그인 콜백 (교환코드 발급)",
            description =
                    """
                    카카오 OAuth 인증 완료 후 호출되는 콜백입니다.

                    - 카카오 인가코드(code)로 로그인/회원가입 처리 후 AT/RT를 생성합니다.
                    - 생성된 토큰쌍은 1회용 교환코드(예: TTL 60초)에 임시 저장합니다.
                    - 최종적으로 302 Redirect로 `kkukmoa://oauth?code={exchangeCode}` 딥링크로 이동합니다.
                    """)
    @ApiErrorCodeExamples({
        ErrorStatus.INVALID_PARAMETER,
        ErrorStatus.KAKAO_API_FAILED,
        ErrorStatus.EXCHANGE_CODE_DUPLICATE,
        ErrorStatus.EXCHANGE_CODE_SERIALIZE_FAIL,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    public ResponseEntity<Void> callback(@RequestParam("code") String kakaoCode) {
        // 1) 카카오 로그인 처리 & 토큰쌍 생성(AT/RT)
        UserResponseDto.loginDto login = userCommandService.loginOrRegisterByKakao(kakaoCode);
        TokenResponseDto tokens = login.getTokenResponseDto(); // accessToken, refreshToken 포함

        // 2) 1회용 교환코드 생성 & 저장 (TTL 예: 60초)
        String exchangeCode = UUID.randomUUID().toString();
        authExchangeRepository.save(exchangeCode, tokens, Duration.ofSeconds(60));

        // 3) 앱으로는 교환코드만 전달
        String redirectUri =
                "kkukmoa://oauth?code=" + UriUtils.encode(exchangeCode, StandardCharsets.UTF_8);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUri)).build();
    }

    @Operation(
            summary = "1회용 교환코드 → AT/RT 교환",
            description =
                    """
                    딥링크로 전달받은 교환코드(code)를 AT/RT로 교환합니다.

                    - 교환코드는 1회용이며 사용 즉시 삭제됩니다.
                    - TTL 만료 또는 이미 사용된 코드는 유효하지 않습니다.
                    - 성공 시 ApiResponse.data에 { accessToken, refreshToken } 형태로 반환합니다.
                    """)
    @ApiErrorCodeExamples({
        ErrorStatus.INVALID_PARAMETER, // code 누락/공백
        ErrorStatus.EXCHANGE_CODE_INVALID, // 키 없음/만료/이미 사용됨
        ErrorStatus.EXCHANGE_CODE_DESERIALIZE_FAIL,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    @PostMapping("/exchange")
    public  ResponseEntity<ApiResponse<TokenResponseDto>> exchange(@RequestParam("code") String code) {
        TokenResponseDto tokens = authExchangeRepository.find(code);

        // 1회용 → 즉시 삭제
        authExchangeRepository.delete(code);

        // (선택) 여기서 RT 회전까지 수행해 내려가도 됨
        return ResponseEntity.ok(ApiResponse.onSuccess(tokens)); // JSON 바디로 AT/RT 반환
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃 API",
            description =
                    "Access Token으로 인증 후 Refresh Token을 삭제하고 Access Token을 블랙리스트에 등록합니다.\n"
                            + "- 클라이언트는 호출 후 저장된 Access Token과 Refresh Token을 모두 제거해야 합니다.",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "성공적으로 로그아웃 처리됨",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "인증 실패 (Access Token이 유효하지 않거나 Refresh Token 불일치)",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorReasonDto.class)))
            })
    @ApiErrorCodeExamples(value = {ErrorStatus.AUTHENTICATION_FAILED})
    public ApiResponse<String> logout(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Parameter(description = "현재 발급된 Refresh Token", required = true)
                    @RequestHeader("refresh-token")
                    String refreshToken,
            @Parameter(description = "현재 발급된 Access Token", required = true)
                    @RequestHeader(HttpHeaders.AUTHORIZATION)
                    String authorizationHeader) {

        // Bearer 제거 후 Access Token만 추출
        String accessToken = authorizationHeader.replace("Bearer ", "").trim();

        // 서비스 호출 (Refresh Token 삭제 + Access Token 블랙리스트 등록)
        userCommandService.logout(user, refreshToken, accessToken);

        return ApiResponse.onSuccess("로그아웃 완료");
    }

    @Operation(
            summary = "JWT 재발급 (AT, 필요 시 RT도 새로 발급)",
            description =
                    """
                    이 API는 리프레시 토큰(Refresh Token)을 사용해 새로운 엑세스 토큰(Access Token)을 발급해줍니다.

                    - 요청 시 Authorization 헤더에 Bearer {refreshToken} 형식으로 넣어주세요.
                    - 리프레시 토큰도 새로 발급될 수 있습니다.
                    - 성공하면 accessToken과 refreshToken을 함께 반환합니다.
                    """)
    @ApiErrorCodeExamples({
        ErrorStatus.REFRESH_TOKEN_REQUIRED,
        ErrorStatus.REFRESH_TOKEN_INVALID,
        ErrorStatus.REFRESH_TOKEN_MISMATCH,
        ErrorStatus.USER_NOT_FOUND,
        ErrorStatus.UNAUTHORIZED,
        ErrorStatus.INTERNAL_SERVER_ERROR
    })
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponseDto>> reissue(HttpServletRequest request) {
        TokenResponseDto tokens = reissueService.reissue(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(tokens));
    }

    @Operation(summary = "로컬 회원가입", description = "이메일/비밀번호로 일반 유저를 생성합니다.")
    @PostMapping("/signup/local")
    public ResponseEntity<ApiResponse<String>> signupLocal(
            @Valid @RequestBody LocalSignupRequest request) {
        userCommandService.registerLocalUser(request);
        return ResponseEntity.ok(ApiResponse.onSuccess("유저 회원가입 성공"));
    }

    @Operation(summary = "로컬 로그인", description = "이메일/비밀번호로 로그인하고 토큰을 발급받습니다.")
    @PostMapping("/login/local")
    public ResponseEntity<TokenResponseDto> loginLocal(
            @Valid @RequestBody LocalLoginRequest request) {
        TokenResponseDto token = userCommandService.loginLocalUser(request);
        return ResponseEntity.ok(token);
    }
    @Operation(
            summary = "사용자의 UUID 생성",
            description = """
            이 API는 사용자의 UUID를 생성하거나, 이미 생성된 UUID를 반환합니다.
            - 요청 시 사용자 UUID가 없으면 새로운 UUID가 생성됩니다.
            - 성공적으로 UUID가 생성되거나 반환되면 ApiResponse에 담아 반환합니다.
            """
    )
    @GetMapping("/uuid")
    public ResponseEntity<ApiResponse<String>> getUserUuid() {
        // 현재 로그인한 사용자의 UUID 생성 또는 반환
        String uuid = userCommandService.createUserUuid();
        // ApiResponse에 UUID를 담아 반환
        return ResponseEntity.ok(ApiResponse.onSuccess(uuid));
    }

}
