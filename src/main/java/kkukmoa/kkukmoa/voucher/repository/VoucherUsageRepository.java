package kkukmoa.kkukmoa.voucher.repository;

import kkukmoa.kkukmoa.voucher.domain.VoucherUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long> {
}

