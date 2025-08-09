package kkukmoa.kkukmoa.store.repository;

import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.enums.StoreStatus;
import kkukmoa.kkukmoa.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByMerchantNumber(String merchantNumber);

    Optional<Store> findByOwner(User owner);

    List<Store> findAllByCategory(Category category);

    List<Store> findByNameContainingIgnoreCase(String name);

    Optional<Store> findByCategory(Category category);

    @Query(
            """
            SELECT store, stamp FROM Store store
            JOIN FETCH Stamp stamp ON stamp.store.id = :storeId AND stamp.user = :user
            WHERE store.id = :storeId
            """)
    Optional<Store> findStoreAndStamp(@Param("storeId") Long storeId, @Param("user") User user);


    // owner의 식별자(id)만 갖고 있을 때 쓰는 버전 (중첩 경로 탐색)
    boolean existsByOwner_IdAndStatus(Long ownerId, StoreStatus status);

    // 도메인 용어를 담은 래퍼: "PENDING 존재 여부"
    default boolean existsPending(Long ownerId, StoreStatus status) {
        // 필요하면 status 파라미터를 없애고 StoreStatus.PENDING으로 고정해도 됨
        return existsByOwner_IdAndStatus(ownerId, status);
    }

    boolean existsByOwner(User owner);
}
