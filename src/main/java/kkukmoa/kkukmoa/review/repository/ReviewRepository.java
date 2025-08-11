package kkukmoa.kkukmoa.review.repository;

import kkukmoa.kkukmoa.review.domain.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByStoreIdOrderByCreatedAtDesc(Long storeId, Pageable pageable);

    Optional<Review> findById(Long id);

    boolean existsByWriterIdAndStoreId(Long userId, Long storeId);

    long countByStoreId(Long storeId);

    Page<Review> findByWriterIdOrderByCreatedAtDesc(Long writerId, Pageable pageable);

    default List<Review> findPreview(Long storeId, int limit) {
        return findByStoreIdOrderByCreatedAtDesc(
                        storeId,
                        PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt")))
                .getContent();
    }
}
