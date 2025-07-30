package kkukmoa.kkukmoa.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import kkukmoa.kkukmoa.apiPayload.code.ErrorReasonDto;
import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.common.util.swagger.ApiErrorCodeExamples;
import kkukmoa.kkukmoa.config.security.JwtTokenProvider;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.UserResponseDto;
import kkukmoa.kkukmoa.user.repository.RefreshTokenRepository;
import kkukmoa.kkukmoa.user.service.UserCommandService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "사용자 API", description = "사용자 관련 API 입니다.")
public class UserController {

    private final UserCommandService userCommandService;

    @GetMapping("/oauth/kakao")
    @Operation(
            summary = "로그인 및 토큰 발급 API",
            description =
                    "\"code\" 와 함께 요청 시 기존/신규 유저를 구분하고 AccessToken을 응답 헤더에 담아 반환합니다.\n"
                            + "- isNewUser: false (기존 유저, DB 조회 확인됨)\n"
                            + "- isNewUser: true  (신규 유저, DB에 없음)",
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "성공적으로 토큰 발급 및 유저 정보 반환",
                        headers = {
                            @Header(
                                    name = HttpHeaders.AUTHORIZATION,
                                    description = "JWT access token",
                                    schema = @Schema(type = "string"))
                        },
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        implementation =
                                                                UserResponseDto.loginDto.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "Invalid Parameter (예: code 누락)",
                        content = @Content(mediaType = "application/json")),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "500",
                        description = "Internal Server Error (카카오 API 오류 또는 DB 처리 실패)",
                        content = @Content(mediaType = "application/json"))
            })
    public ResponseEntity<ApiResponse<UserResponseDto.loginDto>> callback(
            @RequestParam("code") String code) {
        UserResponseDto.loginDto userResponse = userCommandService.loginOrRegisterByKakao(code);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + userResponse.getTokenResponseDto().getAccessToken())
                .body(ApiResponse.onSuccess(userResponse));
    }



    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃 API",
            description = "Access Token으로 인증 후 Refresh Token을 삭제하고 Access Token을 블랙리스트에 등록합니다.\n" +
                    "- 클라이언트는 호출 후 저장된 Access Token과 Refresh Token을 모두 제거해야 합니다.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 로그아웃 처리됨",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "인증 실패 (Access Token이 유효하지 않거나 Refresh Token 불일치)",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorReasonDto.class))
                    )
            }
    )
    @ApiErrorCodeExamples(value = {ErrorStatus.AUTHENTICATION_FAILED})
    public ApiResponse<String> logout(
            @Parameter(hidden = true)
            @AuthenticationPrincipal User user,
            @Parameter(description = "현재 발급된 Refresh Token", required = true)
            @RequestHeader("refresh-token") String refreshToken,
            @Parameter(description = "현재 발급된 Access Token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        // Bearer 제거 후 Access Token만 추출
        String accessToken = authorizationHeader.replace("Bearer ", "").trim();

        // 서비스 호출 (Refresh Token 삭제 + Access Token 블랙리스트 등록)
        userCommandService.logout(user, refreshToken, accessToken);

        return ApiResponse.onSuccess("로그아웃 완료");
    }

}
