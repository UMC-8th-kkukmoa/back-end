package kkukmoa.kkukmoa.review.dto.response;

public record ReviewCursorResponse(ReviewHeaderDto header, CursorPage<ReviewSummaryDto> page) {}
