package kkukmoa.kkukmoa.stamp.repository;

import java.util.Optional;
import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.stamp.domain.Stamp;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StampRepository extends JpaRepository<Stamp, Long> {

    Optional<Stamp> findByUserAndStore(User user, Store store);

    @Query(
        """
          SELECT p, s, u FROM Stamp p
          JOIN FETCH Store s ON p.store = s
          JOIN FETCH User u ON p.user = :user
          WHERE p.user = u AND s.category = :category
        """
    )
    List<Stamp> findByCategoryAndUser(@Param("category") Category category, @Param("user") User user);

}
