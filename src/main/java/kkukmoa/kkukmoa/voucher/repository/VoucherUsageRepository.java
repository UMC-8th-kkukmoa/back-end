package kkukmoa.kkukmoa.voucher.repository;

import kkukmoa.kkukmoa.voucher.domain.VoucherUsage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long> {
    // 현재 사용자 사용내역 (최신순)
    Page<VoucherUsage> findByUserIdOrderByUsedAtDesc(Long userId, Pageable pageable);

    // 기간 + 가게 필터 (옵션)
    @Query(
            """
                select vu
                from VoucherUsage vu
                where vu.user.id = :userId
                  and (:storeId is null or vu.store.id = :storeId)
                  and (:from is null or vu.usedAt >= :from)
                  and (:to   is null or vu.usedAt <  :to)
                order by vu.usedAt desc
            """)
    Page<VoucherUsage> searchMyUsages(
            @Param("userId") Long userId,
            @Param("storeId") Long storeId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}
