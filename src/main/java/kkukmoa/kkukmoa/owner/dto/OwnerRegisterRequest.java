package kkukmoa.kkukmoa.owner.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;

import kkukmoa.kkukmoa.category.domain.CategoryType;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class OwnerRegisterRequest {

    // 입점 신청

    @Schema(description = "매장 이름", example = "스타벅스 강남점")
    @NotBlank
    private String storeName;

    @Schema(description = "매장 주소", example = "서울특별시 강남구 테헤란로 123")
    @NotBlank
    private String storeAddress;

    @Schema(description = "매장 상세 주소", example = "2층 203호")
    private String storeAddressDetail;

    @Schema(description = "위도", example = "37.4979")
    private Double latitude;

    @Schema(description = "경도", example = "127.0276")
    private Double longitude;

    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "영업 시작 시간", example = "09:00", type = "string", format = "HH:mm")
    private LocalTime openingHours;

    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "영업 종료 시간", example = "22:00", type = "string", format = "HH:mm")
    private LocalTime closingHours;

    @Schema(description = "매장 전화번호", example = "0212345678")
    @NotBlank
    private String storePhoneNumber;

    @Schema(description = "매장 이미지 URL", example = "https://image.com/store.jpg")
    private String storeImageUrl;

    @Schema(description = "카테고리", example = "CAFE")
    private CategoryType category;
}
