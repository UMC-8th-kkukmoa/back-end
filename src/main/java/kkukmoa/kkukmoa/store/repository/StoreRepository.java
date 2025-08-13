package kkukmoa.kkukmoa.store.repository;

import kkukmoa.kkukmoa.admin.dto.PendingStoreSummary;
import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.enums.StoreStatus;
import kkukmoa.kkukmoa.user.domain.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    // 반경 내 전체
    // language=SQL
    @Query(
            value =
                    """
                    SELECT s.*
                    FROM store s
                    JOIN region r ON r.id = s.region_id
                    WHERE ST_Distance_Sphere(
                            r.location,
                            ST_SRID(POINT(:lon, :lat), 4326)
                          ) <= :radiusMeters
                    """,
            countQuery =
                    """
                    SELECT COUNT(*)
                    FROM store s
                    JOIN region r ON r.id = s.region_id
                    WHERE ST_Distance_Sphere(
                            r.location,
                            ST_SRID(POINT(:lon, :lat), 4326)
                          ) <= :radiusMeters
                    """,
            nativeQuery = true)
    Page<Store> findWithinRadiusPoint(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radiusMeters") double radiusMeters,
            Pageable pageable);

    // 반경 + 카테고리
    // language=SQL
    @Query(
            value =
                    """
                    SELECT s.*
                    FROM store s
                    JOIN region r ON r.id = s.region_id
                    WHERE s.category_id = :categoryId
                      AND ST_Distance_Sphere(
                            r.location,
                            ST_SRID(POINT(:lon, :lat), 4326)
                          ) <= :radiusMeters
                    """,
            countQuery =
                    """
                    SELECT COUNT(*)
                    FROM store s
                    JOIN region r ON r.id = s.region_id
                    WHERE s.category_id = :categoryId
                      AND ST_Distance_Sphere(
                            r.location,
                            ST_SRID(POINT(:lon, :lat), 4326)
                          ) <= :radiusMeters
                    """,
            nativeQuery = true)
    Page<Store> findWithinRadiusPointByCategory(
            @Param("categoryId") Long categoryId,
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radiusMeters") double radiusMeters,
            Pageable pageable);

    // 반경 + 이름 부분일치 (대소문자 무시)
    // language=SQL
    @Query(
            value =
                    """
                    SELECT s.*
                    FROM store s
                    JOIN region r ON r.id = s.region_id
                    WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))
                      AND ST_Distance_Sphere(
                            r.location,
                            ST_SRID(POINT(:lon, :lat), 4326)
                          ) <= :radiusMeters
                    """,
            countQuery =
                    """
                    SELECT COUNT(*)
                    FROM store s
                    JOIN region r ON r.id = s.region_id
                    WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))
                      AND ST_Distance_Sphere(
                            r.location,
                            ST_SRID(POINT(:lon, :lat), 4326)
                          ) <= :radiusMeters
                    """,
            nativeQuery = true)
    Page<Store> findWithinRadiusPointByName(
            @Param("name") String name,
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("radiusMeters") double radiusMeters,
            Pageable pageable);

    Optional<Store> findByMerchantNumber(String merchantNumber);

    Optional<Store> findByOwner(User owner);

    Optional<Store> findByCategory(Category category);

    @Query(
            """
            SELECT store, stamp FROM Store store
            JOIN FETCH Stamp stamp ON stamp.store.id = :storeId AND stamp.user = :user
            WHERE store.id = :storeId
            """)
    Optional<Store> findStoreAndStamp(@Param("storeId") Long storeId, @Param("user") User user);

    boolean existsByOwner_IdAndStatus(Long ownerId, StoreStatus status);

    default boolean existsPending(Long ownerId) {
        return existsByOwner_IdAndStatus(ownerId, StoreStatus.PENDING);
    }

    boolean existsByOwner(User owner);

    @Query(
            """
                select s.id as storeId,
                       u.email as ownerEmail,
                       s.createdAt as appliedAt
                from Store s
                join s.owner u
                where s.status = :status
                order by s.createdAt desc
            """)
    Page<PendingStoreSummary> findByStatus(@Param("status") StoreStatus status, Pageable pageable);

    Optional<Store> findByIdAndStatus(Long id, StoreStatus status);
}
