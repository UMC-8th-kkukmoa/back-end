package kkukmoa.kkukmoa.review.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "리뷰 헤더 가게 사진 DTO")
public record ReviewHeaderDto (
        Long StoreId,
        String storeName,
        String storeImageUrl

){}


