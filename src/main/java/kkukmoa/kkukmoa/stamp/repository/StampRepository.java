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
                JOIN FETCH stamp.store store
                JOIN FETCH stamp.user user
                WHERE user.id = :userId AND store.id = :storeId
            """)
    Optional<Stamp> findByUserAndStore(
            @Param("userId") Long userId, @Param("storeId") Long storeId);

    @Query(
            """
              SELECT DISTINCT p FROM Stamp p
              LEFT JOIN FETCH p.store s
              WHERE p.user = :user AND s.category = :category
            """)
    List<Stamp> findByCategoryAndUser(
            @Param("category") Category category, @Param("user") User user);

    @Query(
            """
              SELECT DISTINCT p FROM Stamp p
              LEFT JOIN FETCH p.store s
              WHERE p.user = :user
            """)
    List<Stamp> findByUser(@Param("user") User user);
}
