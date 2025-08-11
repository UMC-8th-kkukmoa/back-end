package kkukmoa.kkukmoa.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "리뷰 프리뷰 카드 DTO")
public record ReviewCardDto(

        @Schema(description = "리뷰 ID", example = "101")
        Long reviewId,

        @Schema(description = "리뷰 작성자 닉네임", example = "미진")
        String writerNickname,

        @Schema(description = "썸네일 이미지 URL(첫 번째 이미지, 없으면 null)",
                example = "https://bucket.s3.ap-northeast-2.amazonaws.com/reviews/42/7/uuid.jpg",
                nullable = true)
        String thumbnailUrl,

        @Schema(description = "리뷰 본문 요약(앞 40~60자 정도만 잘라서 전송)",
                example = "여기 맛있어요. 젤 맛있는 곳. 분위기도 좋고 사장님이…")
        String contentSnippet,

        @Schema(description = "리뷰 작성 시각", example = "2025-08-11T15:30:00")
        LocalDateTime createdAt
) {}