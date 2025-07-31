package kkukmoa.kkukmoa.stamp.repository;

import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.stamp.domain.Coupon;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByStore(Store store);

    @Query(
            "SELECT c FROM Coupon c "
                    + "JOIN FETCH c.user u "
                    + "JOIN FETCH c.store s "
                    + "JOIN FETCH s.owner o "
                    + "WHERE c.qrCode = :qrcode")
    Optional<Coupon> findByQrFetchUserAndStore(@Param("qrcode") String qrcode);

    @Query(
            """
              SELECT c FROM Coupon c
              JOIN FETCH Store s ON c.store = s
              JOIN FETCH User u ON c.user = :user
              WHERE c.user = u AND s.category = :category AND c.status = kkukmoa.kkukmoa.stamp.enums.CouponStatus.UNUSED
            """)
    List<Coupon> findByCategoryAndUser(
            @Param("category") Category category, @Param("user") User user);
}
