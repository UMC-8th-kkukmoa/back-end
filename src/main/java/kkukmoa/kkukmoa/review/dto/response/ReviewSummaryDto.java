package kkukmoa.kkukmoa.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

// 조회 요약
@Schema(description = "리뷰 요약 DTO")
public record ReviewSummaryDto(
        @Schema(description = "리뷰 ID", example = "101") Long reviewId,
        @Schema(description = "리뷰 작성자(회원) ID", example = "3") Long writerId,
        @Schema(description = "리뷰 작성자 닉네임", example = "홍길동") String writerNickname,
        @Schema(description = "리뷰 내용", example = "분위기가 너무 좋고 서비스가 친절했어요!") String content,
        @Schema(
                        description = "리뷰 이미지 URL 목록",
                        example =
                                "[\"https://bucket.s3.ap-northeast-2.amazonaws.com/reviews/uuid1.jpg\","
                                    + " \"https://bucket.s3.ap-northeast-2.amazonaws.com/reviews/uuid2.jpg\"]")
                List<String> imageUrls,
        @Schema(description = "리뷰 작성일시", example = "2025-08-11T15:30:00")
                LocalDateTime createdAt) {}
