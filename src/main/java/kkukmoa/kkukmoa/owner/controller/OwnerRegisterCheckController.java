package kkukmoa.kkukmoa.owner.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.owner.dto.request.OwnerLoginRequest;
import kkukmoa.kkukmoa.owner.dto.response.OwnerRegisterCheckResponse;
import kkukmoa.kkukmoa.owner.service.OwnerQueryService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "입점신청 상태 조회(소셜로그인/로컬로그인)", description = "연락처/비밀번호로 본인 확인 후, PENDING 상태 신청 존재 여부만 확인")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class OwnerRegisterCheckController {

    private final OwnerQueryService service;

    @Operation(
            summary = "PENDING 존재여부 확인 (로컬 로그인)",
            description =
                    """
                    연락처와 비밀번호를 통해 본인 인증 후,
                    PENDING 상태의 입점신청이 존재하면 pending=true, 아니면 false를 반환합니다.
                    """)
    @PostMapping("/public/registrations/check-pending")
    public ApiResponse<OwnerRegisterCheckResponse> checkPending(
            @Valid @RequestBody OwnerLoginRequest request) {
        return ApiResponse.onSuccess(service.checkPending(request));
    }

    @Operation(summary = "PENDING 존재여부 확인 (로그인)", description = "현재 로그인한 사용자 기준 PENDING 상태 여부 반환")
    @GetMapping("/owners/registrations/check-pending")
//    @PreAuthorize("hasAuthority('PENDING_OWNER')")d
    public ApiResponse<OwnerRegisterCheckResponse> checkPendingLogin(
            @AuthenticationPrincipal(expression = "id") Long userId) {
        return ApiResponse.onSuccess(service.checkPendingForUser(userId));
    }
}
