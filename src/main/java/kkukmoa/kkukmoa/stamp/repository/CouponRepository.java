package kkukmoa.kkukmoa.stamp.repository;

import java.util.List;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
import kkukmoa.kkukmoa.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

  List<Coupon> findByStore(Store store);
}
