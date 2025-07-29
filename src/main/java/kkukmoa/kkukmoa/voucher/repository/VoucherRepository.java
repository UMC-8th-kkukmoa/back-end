package kkukmoa.kkukmoa.voucher.repository;

import kkukmoa.kkukmoa.voucher.domain.Voucher;
import kkukmoa.kkukmoa.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    Optional<Voucher> findByQrCodeUuidAndUser(String qrCodeUuid, User user);
    Optional<Voucher> findByQrCodeUuid(String qrCodeUuid);
    List<Voucher> findByUser(User user);


}
