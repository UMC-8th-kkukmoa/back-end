package kkukmoa.kkukmoa.review.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.ReviewHandler;
import kkukmoa.kkukmoa.review.domain.Review;
import kkukmoa.kkukmoa.review.domain.ReviewImage;
import kkukmoa.kkukmoa.review.dto.CreateReviewResponse;
import kkukmoa.kkukmoa.review.dto.ReviewSummaryDto;
import kkukmoa.kkukmoa.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 역할: 가게별 리뷰 목록/생성 직후 단건 응답 DTO 구성
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService {

    private final ReviewRepository reviewRepository;

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

    private ReviewSummaryDto toSummary(Review r) {
        return new ReviewSummaryDto(
                r.getId(),
                r.getWriter().getId(),
                r.getWriter().getNickname(), // 표시용(선택)
                r.getContent(),
                r.getImages().stream().map(ReviewImage::getImageUrl).toList(),
                r.getCreatedAt());
    }
}
