package kkukmoa.kkukmoa.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kkukmoa.kkukmoa.admin.service.StoreRegistrationApprovalService;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "관리자 API", description = "관리자 권한이 필요합니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class AdminController {

    private final StoreRegistrationApprovalService approvalService;


    @Operation(summary = "입점 신청 승인", description = "관리자가 입점 신청을 승인하고, 가게를 등록합니다.")
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/store-registrations/{registrationId}/approve")
    public ApiResponse<?> approveStoreRegistration(@PathVariable Long registrationId) {
        approvalService.approveStoreRegistration(registrationId);
        return ApiResponse.onSuccess("입점 신청이 승인되었습니다.");
    }
}