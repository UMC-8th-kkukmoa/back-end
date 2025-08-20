package kkukmoa.kkukmoa.voucher.repository;

import kkukmoa.kkukmoa.voucher.dto.VoucherUsageRow;

import java.time.LocalDateTime;
import java.util.List;

public interface VoucherUsageQdslRepository {
    List<VoucherUsageRow> searchMyUsagesByCursor(
            Long userId,
            LocalDateTime fromDt,
            LocalDateTime toDt,
            LocalDateTime cursorUsedAt,
            Long cursorId,
            int limit);
}
