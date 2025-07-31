package kkukmoa.kkukmoa.store.repository;

import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.user.domain.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByMerchantNumber(String merchantNumber);

    Optional<Store> findByOwner(User owner);

    List<Store> findAllByCategory(Category category);

    Optional<Store> findByCategory(Category category);

    @Query(
        """
        SELECT store, stamp FROM Store store
        JOIN FETCH Stamp stamp ON stamp.store.id = :storeId AND stamp.user = :user
        WHERE store.id = :storeId
        """)
    Optional<Store> findStoreAndStamp(@Param("storeId") Long storeId, @Param("user") User user);
}
