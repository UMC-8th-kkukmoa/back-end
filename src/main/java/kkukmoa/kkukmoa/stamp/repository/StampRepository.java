package kkukmoa.kkukmoa.stamp.repository;

import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.stamp.domain.Stamp;
import kkukmoa.kkukmoa.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StampRepository extends JpaRepository<Stamp, Long> {

    @Query(
            """
            SELECT stamp FROM Stamp stamp
            JOIN FETCH Store store ON store.id = :storeId AND stamp.store = store
            WHERE stamp.user = :user
            """)
    Optional<Stamp> findByUserAndStore(@Param("user") User user, @Param("storeId") Long storeId);

    @Query(
            """
              SELECT DISTINCT p FROM Stamp p
              LEFT JOIN FETCH User u ON p.user = :user
              LEFT JOIN FETCH p.store
              WHERE p.user = :user AND p.store.category = :category
            """)
    List<Stamp> findByCategoryAndUser(
            @Param("category") Category category, @Param("user") User user);

    @Query(
        """
          SELECT DISTINCT p FROM Stamp p
          LEFT JOIN FETCH User u ON p.user = :user
          LEFT JOIN FETCH p.store
          WHERE p.user = :user
        """
    )
    List<Stamp> findByUser(@Param("user")  User user);

}
