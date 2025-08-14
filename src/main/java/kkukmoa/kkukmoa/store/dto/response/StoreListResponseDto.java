package kkukmoa.kkukmoa.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class StoreListResponseDto {
    private Long storeId;
    private String name;
    private String openingHours;
    private String closingHours;
    private String storeImage;
    private Double latitude;
    private Double longitude;
    private String categoryName;
    private Integer reviewCount;
    private Double distance;
    private Boolean liked;
}
