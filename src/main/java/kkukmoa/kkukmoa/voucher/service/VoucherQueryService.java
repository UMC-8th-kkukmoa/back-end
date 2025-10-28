package kkukmoa.kkukmoa.voucher.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.VoucherHandler;
import kkukmoa.kkukmoa.common.util.AuthService;
import kkukmoa.kkukmoa.common.util.CursorUtil;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.voucher.converter.VoucherConverter;
import kkukmoa.kkukmoa.voucher.domain.Voucher;
import kkukmoa.kkukmoa.voucher.dto.VoucherResponseDto;
import kkukmoa.kkukmoa.voucher.dto.VoucherUsageRow;
import kkukmoa.kkukmoa.voucher.repository.VoucherRepository;
import kkukmoa.kkukmoa.voucher.repository.VoucherUsageQdslRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherQueryService {

    private final VoucherRepository voucherRepository;
    private final AuthService authService;
    private final VoucherUsageQdslRepository voucherUsageQdslRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    // "voucher_"

    /** 내 금액권 목록 조회 */
    @Transactional(readOnly = true)
    public List<VoucherResponseDto.VoucherListResponseDto> getMyVouchers() {
        User user = authService.getCurrentUser();

        return voucherRepository.findByUser(user).stream()
                .map(VoucherConverter::toListDto)
                .toList();
    }

    /** 금액권 상세 조회 */
    public VoucherResponseDto.VoucherDetailResponseDto getVoucherDetail(String qrCodeUuid) {
        Voucher voucher =
                voucherRepository
                        .findByQrCodeUuid(qrCodeUuid)
                        .orElseThrow(() -> new VoucherHandler(ErrorStatus.VOUCHER_NOT_FOUND));

        return VoucherConverter.toDetailDto(voucher);
    }

    /** 사용자의 금액권 사용내역 (커서 기반) - 정렬: usedAt DESC, id DESC - 커서: base64url("epochMillis:id") */
    @Transactional(readOnly = true)
    public VoucherResponseDto.CursorPageResponse<VoucherUsageRow> getMyUsagesByCursor(
            Integer limit, String cursor, LocalDate from, LocalDate to) {
        Long userId = authService.getCurrentUser().getId();

        // limit 기본값/상한선
        int pageSize = (limit == null || limit <= 0 || limit > 100) ? 10 : limit;

        // 날짜 범위
        LocalDateTime fromDt = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDt = (to != null) ? to.plusDays(1).atStartOfDay() : null;

        // 커서 파싱
        var parsed = CursorUtil.decode(cursor, ZONE);
        LocalDateTime cursorUsedAt = parsed.map(CursorUtil.Cursor::usedAt).orElse(null);
        Long cursorId = parsed.map(CursorUtil.Cursor::id).orElse(null);

        // QueryDSL로 limit+1 조회(다음 페이지 존재 여부 확인용)
        List<VoucherUsageRow> rows =
                voucherUsageQdslRepository.searchMyUsagesByCursor(
                        userId, fromDt, toDt, cursorUsedAt, cursorId, pageSize);

        boolean hasNext = rows.size() > pageSize;
        if (hasNext) {
            rows = rows.subList(0, pageSize);
        }

        String nextCursor = null;
        if (hasNext && !rows.isEmpty()) {
            VoucherUsageRow last = rows.get(rows.size() - 1);
            nextCursor = CursorUtil.encode(last.getUsedAt(), last.getUsageId(), ZONE);
        }

        return VoucherResponseDto.CursorPageResponse.<VoucherUsageRow>builder()
                .items(rows)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }
}
