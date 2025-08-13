package kkukmoa.kkukmoa.review.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.ReviewHandler;
import kkukmoa.kkukmoa.apiPayload.exception.handler.StoreHandler;
import kkukmoa.kkukmoa.review.domain.Review;
import kkukmoa.kkukmoa.review.domain.ReviewImage;
import kkukmoa.kkukmoa.review.dto.request.CreateReviewResponse;
import kkukmoa.kkukmoa.review.dto.response.*;
import kkukmoa.kkukmoa.review.repository.ReviewCursorRepository;
import kkukmoa.kkukmoa.review.repository.ReviewRepository;

import kkukmoa.kkukmoa.review.util.CursorCodec;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// 역할: 가게별 리뷰 목록/생성 직후 단건 응답 DTO 구성
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;

    private final ReviewCursorRepository reviewCursorRepository;

    private final StoreRepository storeRepository;

    /**
     * [리뷰 작성 직후 응답 데이터 생성]
     * - 리뷰 ID로 Review 엔티티 조회
     * - CreateReviewResponse DTO로 변환하여 반환
     *
     * @param id 생성된 리뷰의 ID
     * @return 리뷰 생성 결과 DTO
     */
    public CreateReviewResponse getCreateResponse(Long id) {
        Review r =
                reviewRepository
                        .findById(id)
                        .orElseThrow(() -> new ReviewHandler(ErrorStatus.REVIEW_NOT_FOUND));
        return new CreateReviewResponse(
                r.getId(),
                r.getStore().getId(),
                r.getWriter().getId(),
                r.getContent(),
                r.getImages().stream().map(ReviewImage::getImageUrl).toList(),
                r.getCreatedAt());
    }

    /**
     * [리뷰 요약 변환]
     * - Review 엔티티를 ReviewSummaryDto로 변환
     * - 작성자 닉네임은 표시용으로 포함
     *
     * @param r Review 엔티티
     * @return ReviewSummaryDto
     */
    private ReviewSummaryDto toSummary(Review r) {
        return new ReviewSummaryDto(
                r.getId(),
                r.getWriter().getId(),
                r.getWriter().getNickname(), // 표시용(선택)
                r.getContent(),
                r.getImages().stream().map(ReviewImage::getImageUrl).toList(),
                r.getCreatedAt());
    }

    /**
     * [리뷰 미리보기 조회]
     * - 지정된 가게의 최신 리뷰 중 일부(limit)만 조회
     * - 대표 이미지(첫 번째 이미지)와 내용 일부(snippet)만 포함
     *
     * @param storeId 대상 가게 ID
     * @param limit   가져올 리뷰 개수
     * @return ReviewCardDto 리스트
     */
    public List<ReviewCardDto> getPreview(Long storeId, int limit) {
        return reviewRepository.findPreview(storeId, limit).stream()
                .map(
                        r ->
                                new ReviewCardDto(
                                        r.getId(),
                                        r.getWriter().getNickname(),
                                        r.getImages().isEmpty()
                                                ? null
                                                : r.getImages().get(0).getImageUrl(),
                                        snippet(r.getContent(), 10),
                                        r.getCreatedAt()))
                .toList();
    }

    /**
     * [리뷰 내용 요약]
     * - 지정된 길이(len)까지만 잘라서 표시
     * - 길이를 초과하면 "..." 추가
     *
     * @param s   원본 문자열
     * @param len 최대 길이
     * @return 요약 문자열
     */
    private String snippet(String s, int len) {
        if (s == null) return null;
        return s.length() <= len ? s : s.substring(0, len) + "...";
    }

    /**
     * [가게별 리뷰 개수 조회]
     * - storeId 기준 리뷰 총 개수 반환
     *
     * @param storeId 가게 ID
     * @return 리뷰 개수
     */
    public long countByStore(Long storeId) {
        return reviewRepository.countByStoreId(storeId);
    }

    /**
     * [커서 기반 리뷰 목록 조회]
     * - 무한 스크롤 방식 지원
     * - 첫 페이지 여부에 따라 처리 분기
     * - 커서는 "createdAt|id" 형식의 문자열을 Base64 인코딩하여 전달/해석
     * - 첫 페이지에는 리뷰 목록 + 가게 헤더 정보 포함
     *
     * @param storeId 대상 가게 ID
     * @param cursor  다음 페이지 시작점 식별자 (null이면 첫 페이지)
     * @param size    페이지 크기
     * @return ReviewCursorResponse (리뷰 목록, 다음 커서, 헤더)
     */
    public ReviewCursorResponse listByCursor(Long storeId, String cursor, int size) {
        List<Review> reviews;

        // 첫 페이지 여부 플래그
        boolean firstPage = (cursor == null || cursor.isBlank());

        if (firstPage) {
            // 첫 로드: 최신순 페이지네이션 시작
            var pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            reviews =
                    reviewRepository
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
            reviews = reviewCursorRepository.fetchNext(storeId, createdAt, id, size);
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
            Store store =
                    storeRepository
                            .findById(storeId)
                            .orElseThrow(() -> new StoreHandler(ErrorStatus.STORE_NOT_FOUND));

            // 이미지 필드 네이밍은 프로젝트에 맞춰 교체 (예: getMainImageUrl / getThumbnailUrl 등)
            String imageUrl = store.getStoreImage();
            header = new ReviewHeaderDto(store.getId(), store.getName(), imageUrl);
        }

        return new ReviewCursorResponse(header, page);
    }
}
