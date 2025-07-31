package kkukmoa.kkukmoa.stamp.dto.stampDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class StampResponseDto {

    @Builder
    @Getter
    @Schema(description = "스탬프 목록 DTO")
    public static class StampListDto {
        @Schema(description = "스탬프 목록")
        List<StampDto> stamps;

        @Schema(description = "조회된 스탬프 개수", example = "4")
        Integer total;
        // TODO 스탬프 카테고리 추가하기
    }

    @Builder
    @Getter
    @Schema(description = "단일 스탬프 정보 DTO")
    public static class StampDto {
        @JsonProperty(value = "id")
        @Schema(description = "스탬프 식별자", example = "1")
        Long id;

        @JsonProperty(value = "store_name")
        @Schema(description = "가게명", example = "미진카페")
        String storeName;

        @JsonProperty(value = "stamp_score")
        @Schema(description = "스탬프 점수", example = "8")
        Integer stampScore;
    }

    @Builder
    @Getter
    public static class StampSaveDto {
        @JsonProperty(value = "has_earned_coupon")
        Boolean hasEarnedCoupon;
    }

}
