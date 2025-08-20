package kkukmoa.kkukmoa.voucher.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.common.util.swagger.ApiErrorCodeExamples;
import kkukmoa.kkukmoa.voucher.dto.VoucherResponseDto;
import kkukmoa.kkukmoa.voucher.dto.VoucherUsageRow;
import kkukmoa.kkukmoa.voucher.service.VoucherQueryService;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vouchers")
@Tag(name = "금액권 API", description = "금액권 관련 API 입니다.")
public class VoucherController {

    private final VoucherQueryService voucherQueryService;

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

    /**
     * 내 금액권 사용내역 조회 (커서 기반 무한스크롤) - 최신 사용내역부터 정렬 (usedAt DESC, id DESC) - 커서:
     * base64url("epochMillis:id") 형식
     */
    @Operation(
            summary = "내 금액권 사용내역 조회 (커서 기반 무한스크롤)",
            description =
                    """
                     로그인한 사용자의 금액권 사용내역을 **커서 기반 무한스크롤** 방식으로 조회합니다.

                     🔹 정렬 기준: `usedAt DESC`, `id DESC` \s
                     🔹 기본 조회: 최신 순부터 10개 조회 \s
                     🔹 커서(cursor): 서버 응답의 `nextCursor` 값을 그대로 다음 요청에 사용 \s
                         - 커서 형식: base64url("epochMillis:id") \s
                         - 클라이언트가 직접 만들 필요 없음 (서버 응답값 사용)

                     🔹 날짜 필터링: 선택적으로 사용 가능 \s
                         - `from`: 조회 시작일 (포함), 형식 `yyyy-MM-dd` \s
                         - `to`: 조회 종료일 (포함), 형식 `yyyy-MM-dd` \s
                         - 미입력 시 전체 기간에서 최신순으로 조회

                     🔹 limit:
                         - 요청당 최대 조회 개수 지정 (기본 10개, 최대 100개)
                         - ex) `?limit=20` → 20개 조회

                     ✨ 예시 요청:
                     - `/v1/vouchers/usage` (최신순 10개)
                     - `/v1/vouchers/usage?cursor=MT7255...:123&from=2025-08-01&to=2025-08-31&limit=10`
                    \s\
                    """)
    @GetMapping("/usage")
    public ResponseEntity<ApiResponse<VoucherResponseDto.CursorPageResponse<VoucherUsageRow>>>
            getMyUsage(
                    @Parameter(description = "페이지당 조회 개수 (최대 100)", example = "5")
                            @RequestParam(required = false)
                            Integer limit,
                    @Parameter(
                                    description = "커서 (base64url(\"epochMillis:id\") 형식)",
                                    example = "MT725500000000:123")
                            @RequestParam(required = false)
                            String cursor,
                    @Parameter(description = "조회 시작 날짜 (yyyy-MM-dd)", example = "2025-08-01")
                            @RequestParam(required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            LocalDate from,
                    @Parameter(description = "조회 종료 날짜 (yyyy-MM-dd)", example = "2025-08-20")
                            @RequestParam(required = false)
                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            LocalDate to) {
        var data = voucherQueryService.getMyUsagesByCursor(limit, cursor, from, to);
        return ResponseEntity.ok(ApiResponse.onSuccess(data));
    }
}
