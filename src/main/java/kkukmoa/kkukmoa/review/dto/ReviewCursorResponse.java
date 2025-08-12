package kkukmoa.kkukmoa.review.dto;

public record ReviewCursorResponse(ReviewHeaderDto header, CursorPage<ReviewSummaryDto> page) {}
