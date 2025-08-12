package kkukmoa.kkukmoa.review.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.StoreHandler;
import kkukmoa.kkukmoa.review.domain.Review;
import kkukmoa.kkukmoa.review.domain.ReviewImage;
import kkukmoa.kkukmoa.review.dto.CursorPage;
import kkukmoa.kkukmoa.review.dto.ReviewCursorResponse;
import kkukmoa.kkukmoa.review.dto.ReviewHeaderDto;
import kkukmoa.kkukmoa.review.dto.ReviewSummaryDto;
import kkukmoa.kkukmoa.review.repository.ReviewCursorRepository;
import kkukmoa.kkukmoa.review.repository.ReviewRepository;
import kkukmoa.kkukmoa.review.util.CursorCodec;

import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    private final StoreRepository storeRepository;

    public ReviewCursorResponse listByCursor(Long storeId, String cursor, int size) {
        List<Review> reviews;

        // 첫 페이지 여부 플래그
        boolean firstPage = (cursor == null || cursor.isBlank());

        if (firstPage) {
            // 첫 로드: 최신순 페이지네이션 시작
            var pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            reviews = reviewRepository
                    .findByStoreIdOrderByCreatedAtDesc(storeId, pageable)
                    .getContent();
        } else {
            // 다음 페이지: 커서 해석 -> "createdAt|id" 형태
            String raw = CursorCodec.decode(cursor);
            String[] parts = raw.split("\\|");

            // createdAt 직렬화/역직렬화 포맷은 프론트/백엔드가 동일하게 맞춘다는 전제
            LocalDateTime createdAt = LocalDateTime.parse(parts[0]);
            Long id = Long.parseLong(parts[1]);

            // 커서 기준 다음 N개 조회 (동일 createdAt 중복 방지를 위해 (createdAt, id) 튜플 비교 권장)
            reviews = cursorRepository.fetchNext(storeId, createdAt, id, size);
        }

        // 본문 변환
        var content = reviews.stream().map(this::toSummary).toList();

        // 다음 커서 생성
        String nextCursor = null;
        boolean hasNext = reviews.size() == size;
        if (hasNext) {
            Review last = reviews.get(reviews.size() - 1);
            String raw = last.getCreatedAt() + "|" + last.getId();
            nextCursor = CursorCodec.encode(raw);
        }

        // CursorPage 조립
        CursorPage<ReviewSummaryDto> page = new CursorPage<>(content, nextCursor, hasNext);

        // 첫 페이지면 헤더 채우기, 아니면 null
        ReviewHeaderDto header = null;
        if (firstPage) {
            // 가게 정보 로딩 (예: StoreRepository)
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new StoreHandler(ErrorStatus.STORE_NOT_FOUND));

            // 이미지 필드 네이밍은 프로젝트에 맞춰 교체 (예: getMainImageUrl / getThumbnailUrl 등)
            String imageUrl = store.getStoreImage();
            header = new ReviewHeaderDto(store.getId(), store.getName(), imageUrl);
        }

        return new ReviewCursorResponse(header, page);
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
