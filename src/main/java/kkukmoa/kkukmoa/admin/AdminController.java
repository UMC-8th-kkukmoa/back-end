package kkukmoa.kkukmoa.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import kkukmoa.kkukmoa.admin.service.StoreApprovalService;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 API", description = "관리자 권한이 필요합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class AdminController {

    private final StoreApprovalService storeApprovalService;

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
        storeApprovalService.approve(storeId);
        // HTTP 상태코드 200 OK + 바디에 메시지
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccess("입점 신청이 승인되었습니다."));
    }
}
