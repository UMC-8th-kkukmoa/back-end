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
    private int reviewCount;
    private double distance;
}
