package kkukmoa.kkukmoa.review.service;

import kkukmoa.kkukmoa.review.domain.Review;
import kkukmoa.kkukmoa.review.domain.ReviewImage;
import kkukmoa.kkukmoa.review.dto.CursorPage;
import kkukmoa.kkukmoa.review.dto.ReviewSummaryDto;
import kkukmoa.kkukmoa.review.repository.ReviewCursorRepository;
import kkukmoa.kkukmoa.review.repository.ReviewRepository;
import kkukmoa.kkukmoa.review.util.CursorCodec;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewCursorService {

    private final ReviewRepository reviewRepository; // 기존 JPA 리포지토리
    private final ReviewCursorRepository cursorRepository; // 커서 전용 구현

    public CursorPage<ReviewSummaryDto> listByCursor(Long storeId, String cursor, int size) {
        List<Review> reviews;

        if (cursor == null || cursor.isBlank()) {

            var pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            reviews =
                    reviewRepository
                            .findByStoreIdOrderByCreatedAtDesc(storeId, pageable)
                            .getContent();
        } else {
            // 커서 해석: "createdAt|id"
            String raw = CursorCodec.decode(cursor);
            String[] parts = raw.split("\\|");
            LocalDateTime createdAt = LocalDateTime.parse(parts[0]);
            Long id = Long.parseLong(parts[1]);

            reviews = cursorRepository.fetchNext(storeId, createdAt, id, size);
        }

        var content = reviews.stream().map(this::toSummary).toList();

        String nextCursor = null;
        boolean hasNext = reviews.size() == size;
        if (hasNext) {
            Review last = reviews.get(reviews.size() - 1);
            String raw = last.getCreatedAt() + "|" + last.getId();
            nextCursor = CursorCodec.encode(raw);
        }

        return new CursorPage<>(content, nextCursor, hasNext);
    }

    private ReviewSummaryDto toSummary(Review r) {
        return new ReviewSummaryDto(
                r.getId(),
                r.getWriter().getId(),
                r.getWriter().getNickname(),
                r.getContent(),
                r.getImages().stream().map(ReviewImage::getImageUrl).toList(),
                r.getCreatedAt());
    }
}
