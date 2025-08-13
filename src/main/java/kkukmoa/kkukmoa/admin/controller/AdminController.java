package kkukmoa.kkukmoa.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import kkukmoa.kkukmoa.admin.dto.PageDto;
import kkukmoa.kkukmoa.admin.dto.PendingStoreSummary;
import kkukmoa.kkukmoa.admin.service.AdminCommandService;
import kkukmoa.kkukmoa.admin.service.AdminQueryService;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;

import kkukmoa.kkukmoa.owner.dto.response.OwnerRegisterResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "관리자 API", description = "관리자 권한이 필요합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class AdminController {

    private final AdminCommandService adminCommandService;
    private final AdminQueryService adminQueryService;

    @Operation(
            summary = "입점 신청 승인",
            description = "PENDING 상태인 가게를 승인합니다. 가맹점번호가 부여되고 신청자는 점주가 됩니다.")
    // @PreAuthorize("hasRole('ADMIN')")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "승인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "가게 없음(STORE4001)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 상태 전환 또는 이미 승인됨(STORE4101/STORE4004)")
    })
    @PostMapping("/stores/{storeId}/approve")
    public ResponseEntity<ApiResponse<String>> approve(@PathVariable Long storeId) {
        adminCommandService.approve(storeId);
        // HTTP 상태코드 200 OK + 바디에 메시지
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccess("입점 신청이 승인되었습니다."));
    }

    @GetMapping("/pending")
    @Operation(summary = "승인 대기 목록 조회", description = "입점 신청 상태가 PENDING인 항목을 9개/페이지로 조회")
    @PreAuthorize("hasRole('ADMIN')") // 관리자만 접근
    public ApiResponse<PageDto<PendingStoreSummary>> pending(
            @Parameter(description = "0부터 시작") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "기본 9, 최대 50") @RequestParam(defaultValue = "9") int size
    ) {
        Page<PendingStoreSummary> result = adminQueryService.listPending(page, size);
        return ApiResponse.onSuccess(PageDto.from(result)); // 표준 응답 래퍼로 감싸기
    }

    @Operation(summary = "입점신청 단건 상세(대기 중)", description = "PENDING 상태의 특정 신청을 상세 조회합니다.")
    @GetMapping("/pending/{storeId}")
    public ApiResponse<OwnerRegisterResponse> getPendingDetail(
            @Parameter(description = "스토어 ID", example = "137")
            @PathVariable Long storeId
    ) {
        OwnerRegisterResponse dto = adminQueryService.getPendingDetail(storeId);
        return ApiResponse.onSuccess(dto);
    }
}
