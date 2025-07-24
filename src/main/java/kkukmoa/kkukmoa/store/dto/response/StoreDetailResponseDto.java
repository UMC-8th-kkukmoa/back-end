package kkukmoa.kkukmoa.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class StoreDetailResponseDto {
    private Long storeId;
    private String name;
    private int reviewCount;
    private String categoryName;
    private String merchantNumber;
    private String address;
    private String detailAddress;
    private String openingHours;
    private String closingHours;
}
