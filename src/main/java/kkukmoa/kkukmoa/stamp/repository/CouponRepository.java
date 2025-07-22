package kkukmoa.kkukmoa.stamp.repository;

import java.util.List;
import java.util.Optional;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
import kkukmoa.kkukmoa.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

  List<Coupon> findByStore(Store store);

  @Query("SELECT c FROM Coupon c "
      + "JOIN FETCH c.user u "
      + "JOIN FETCH c.store s "
      + "JOIN FETCH s.owner o "
      + "WHERE c.qrCode = :qrcode")
  Optional<Coupon> findByQrFetchUserAndStore(@Param("qrcode") String qrcode);

}
