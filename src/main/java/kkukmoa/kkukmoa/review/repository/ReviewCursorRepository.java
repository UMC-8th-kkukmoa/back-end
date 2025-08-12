package kkukmoa.kkukmoa.review.repository;

import kkukmoa.kkukmoa.review.domain.Review;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewCursorRepository {
    List<Review> fetchNext(Long storeId, LocalDateTime createdAt, Long id, int size);
}
