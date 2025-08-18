package kkukmoa.kkukmoa.voucher.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.common.util.swagger.ApiErrorCodeExamples;
import kkukmoa.kkukmoa.voucher.dto.VoucherResponseDto;
import kkukmoa.kkukmoa.voucher.service.VoucherCommandService;
import kkukmoa.kkukmoa.voucher.service.VoucherQueryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vouchers")
@Tag(name = "금액권 API", description = "금액권 관련 API 입니다.")
public class VoucherController {

    private final VoucherQueryService voucherQueryService;
    private final VoucherCommandService voucherCommandService;

    @Operation(
            summary = "내 금액권 전체 목록 조회",
            description =
                    """
                        로그인한 사용자의 금액권 전체 목록을 조회합니다.

                        - 금액권은 결제 후 발급되며, 이 API는 로그인된 사용자 기준으로 반환합니다.
                        - 각각의 금액권은 QR UUID, 이름, 상태, 유효기간 등의 정보를 포함합니다.
                        - 금액권의 유효기간까지 남은 일수를 반환합니다:
                          - 유효기간이 지난 금액권은 `-1`이 반환됩니다.
                          - 오늘 만료되는 금액권은 `0`이 반환됩니다.
                          - 그 외 유효기간이 남은 금액권은 유효기간까지 남은 일수가 반환됩니다.
                    """)
    @ApiErrorCodeExamples({ErrorStatus.UNAUTHORIZED})
    @GetMapping
    public ResponseEntity<ApiResponse<List<VoucherResponseDto.VoucherListResponseDto>>>
            getMyTickets() {
        List<VoucherResponseDto.VoucherListResponseDto> result =
                voucherQueryService.getMyVouchers();
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }

    @Operation(
            summary = "금액권 상세 조회",
            description =
                    """
                    특정 금액권의 상세 정보를 조회합니다.

                    - QR 코드 UUID를 기반으로 상세 정보를 반환합니다.
                    - 사용 여부, 결제 금액, 발급일자 등 상세 필드를 제공합니다.
                    """)
    @ApiErrorCodeExamples({ErrorStatus.VOUCHER_NOT_FOUND})
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<VoucherResponseDto.VoucherDetailResponseDto>> getTicketDetail(
            @PathVariable String uuid) {
        var detail = voucherQueryService.getVoucherDetail(uuid);
        return ResponseEntity.ok(ApiResponse.onSuccess(detail));
    }

    @GetMapping("/payment")
    public String securedTossPage(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("접근 권한 없음");
        }
        return "toss"; // templates/toss.html (Thymeleaf 등)
    }
}
