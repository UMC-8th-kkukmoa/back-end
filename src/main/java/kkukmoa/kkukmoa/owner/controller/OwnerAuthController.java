package kkukmoa.kkukmoa.owner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.common.util.swagger.ApiErrorCodeExamples;
import kkukmoa.kkukmoa.owner.dto.request.OwnerLoginRequest;
import kkukmoa.kkukmoa.owner.dto.request.OwnerRegisterRequest;
import kkukmoa.kkukmoa.owner.dto.request.OwnerSignupRequest;
import kkukmoa.kkukmoa.owner.service.OwnerCommandService;
import kkukmoa.kkukmoa.user.annotation.CurrentUser;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.TokenResponseDto;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/owners")
@Tag(name = "사장님 회원가입/입점신청 API", description = "사장님 회원가입 및 로그인")
public class OwnerAuthController {

    private final OwnerCommandService ownerCommandService;


    @PostMapping("/register")
    @Operation(
            summary = "사장님 회원가입",
            description =
                    """
                    로컬 로그인 기반 사장님 회원가입 API입니다.

                    - 전화번호와 비밀번호를 입력받아 등록합니다.
                    - (필수) 약관 동의: agreeTerms=true, agreePrivacy=true
                    - 기본 권한은 ROLE_PENDING_OWNER로 부여되며, 입점 신청 이후 승인됩니다.
                    """)
    @ApiErrorCodeExamples(
            value = {
                ErrorStatus.DUPLICATION_PHONE_NUMBER, // 전화번호 중복
                ErrorStatus.INTERNAL_SERVER_ERROR // 서버 오류(공통)
            })
    public ResponseEntity<ApiResponse<String>> registerOwner(
            @RequestBody @Valid OwnerSignupRequest request) {
        ownerCommandService.registerLocalOwner(request);
        return ResponseEntity.ok(ApiResponse.onSuccess("사장님 회원가입 성공"));
    }

    @PostMapping("/login")
    @Operation(summary = "사장님 로그인", description = "전화번호와 비밀번호로 사장님 계정을 로그인합니다.")
    @ApiErrorCodeExamples(value = {ErrorStatus.USER_NOT_FOUND, ErrorStatus.PASSWORD_NOT_MATCH})
    public ResponseEntity<ApiResponse<TokenResponseDto>> loginOwner(
            @RequestBody @Valid OwnerLoginRequest request) {
        TokenResponseDto token = ownerCommandService.loginOwner(request);
        return ResponseEntity.ok(ApiResponse.onSuccess(token));
    }

    @PostMapping("/applications")
    @Operation(summary = "입점 신청 API", description = "로그인한 사장님이 매장을 등록(입점 신청)합니다.")
    @ApiErrorCodeExamples(
            value = {
                ErrorStatus.STORE_CATEGORY_NOT_FOUND,
                ErrorStatus.OWNER_REQUEST_ALREADY_SUBMITTED
            })
    public ResponseEntity<ApiResponse<String>> registerStore(
            @RequestBody @Valid OwnerRegisterRequest request, @CurrentUser User user) {

        ownerCommandService.applyStoreRegistration(user, request);
        return ResponseEntity.ok(ApiResponse.onSuccess("입점 신청이 완료되었습니다."));
    }
}
