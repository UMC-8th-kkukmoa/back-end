package kkukmoa.kkukmoa.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "커서 기반 페이지 응답")
public record CursorPage<T>(
        @Schema(description = "데이터 목록") List<T> content,
        @Schema(description = "다음 페이지 요청에 사용할 커서(없으면 null)", example = "MjAyNS0wOC0xMVQxNTozMDowMHwyMzQ=") String nextCursor,
        @Schema(description = "다음 페이지 존재 여부", example = "true") boolean hasNext
) {}