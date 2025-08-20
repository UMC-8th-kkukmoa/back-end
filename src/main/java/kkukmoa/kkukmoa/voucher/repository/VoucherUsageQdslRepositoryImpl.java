package kkukmoa.kkukmoa.voucher.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kkukmoa.kkukmoa.voucher.domain.QVoucherUsage;
import kkukmoa.kkukmoa.voucher.dto.VoucherUsageRow;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class VoucherUsageQdslRepositoryImpl implements VoucherUsageQdslRepository {
    private final JPAQueryFactory qf;

    @Override
    public List<VoucherUsageRow> searchMyUsagesByCursor(
            Long userId,
            LocalDateTime fromDt,
            LocalDateTime toDt,
            LocalDateTime cursorUsedAt,
            Long cursorId,
            int limit) {
        QVoucherUsage vu = QVoucherUsage.voucherUsage;

        BooleanBuilder where = new BooleanBuilder().and(vu.user.id.eq(userId));

        if (fromDt != null) {
            where.and(vu.usedAt.goe(fromDt));
        }

        if (toDt != null) {
            where.and(vu.usedAt.lt(toDt));
        }

        if (cursorUsedAt != null && cursorId != null) {
            // usedAt DESC, id DESC 순 정렬 기준에 맞는 커서 조건
            where.and(
                    vu.usedAt
                            .lt(cursorUsedAt)
                            .or(vu.usedAt.eq(cursorUsedAt).and(vu.id.lt(cursorId))));
        }

        return qf.select(
                        Projections.constructor(
                                VoucherUsageRow.class,
                                vu.id,
                                vu.voucher.id,
                                vu.store.id,
                                vu.storeName,
                                vu.usedAmount,
                                vu.usedAt))
                .from(vu)
                .where(where)
                .orderBy(vu.usedAt.desc(), vu.id.desc()) // 커서 정렬 기준
                .limit(limit + 1) // 다음 페이지 존재 여부 확인용
                .fetch();
    }
}
