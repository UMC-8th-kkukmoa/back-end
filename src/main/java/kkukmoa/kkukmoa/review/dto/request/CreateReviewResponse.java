package kkukmoa.kkukmoa.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

// 생성 응답
@Schema(description = "리뷰 생성 응답 DTO")
public record CreateReviewResponse(
        @Schema(description = "리뷰 ID", example = "101") Long reviewId,
        @Schema(description = "리뷰가 작성된 가게 ID", example = "15") Long storeId,
        @Schema(description = "리뷰 작성자(회원) ID", example = "3") Long writerId,
        @Schema(description = "리뷰 내용", example = "가게 분위기가 정말 좋고 음식이 맛있었어요!") String content,
        @Schema(
                        description = "리뷰 이미지 URL 목록",
                        example =
                                "[\"https://bucket.s3.ap-northeast-2.amazonaws.com/reviews/uuid1.jpg\","
                                    + " \"https://bucket.s3.ap-northeast-2.amazonaws.com/reviews/uuid2.jpg\"]")
                List<String> imageUrls,
        @Schema(description = "리뷰 작성일시", example = "2025-08-11T15:30:00")
                LocalDateTime createdAt) {}
