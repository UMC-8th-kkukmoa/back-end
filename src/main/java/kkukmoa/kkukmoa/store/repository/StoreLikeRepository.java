package kkukmoa.kkukmoa.store.repository;

import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.store.domain.StoreLike;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StoreLikeRepository extends JpaRepository<StoreLike, Long> {

    boolean existsByUserIdAndStoreId(Long userId, Long storeId);

    Optional<StoreLike> findByUserIdAndStoreId(Long userId, Long storeId);

    long countByStoreId(Long storeId);

    @EntityGraph(attributePaths = {"store", "store.region", "store.category"})
    List<StoreLike> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"store", "store.region", "store.category"})
    @Query("""
           select sl from StoreLike sl
             join sl.store s
             join s.category c
            where sl.user.id = :userId and c.type = :categoryType
           """)
    List<StoreLike> findByUserIdAndCategoryType(@Param("userId") Long userId,
                                                @Param("categoryType") CategoryType categoryType);

    @Query("""
           select sl.store.id
             from StoreLike sl
            where sl.user.id = :userId
              and sl.store.id in :storeIds
           """)
    List<Long> findLikedStoreIds(@Param("userId") Long userId,
                                 @Param("storeIds") Collection<Long> storeIds);
}
