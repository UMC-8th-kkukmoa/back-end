package kkukmoa.kkukmoa.store.converter;

import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.region.domain.Region;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.dto.request.StoreRequestDto;
import kkukmoa.kkukmoa.store.dto.response.StoreDetailResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StoreListResponseDto;

import org.springframework.stereotype.Component;

@Component
public class StoreConverter {

    // 가게 등록
    public Store toStore(StoreRequestDto request, Region region, Category category) {
        return Store.builder()
                .name(request.getName())
                .number(request.getNumber())
                .closingHours(request.getClosingHours())
                .openingHours(request.getOpeningHours())
                .region(region)
                .category(category)
                .storeImage(request.getStoreImage())
                .build();
    }

    // 가게 목록 조회 응답
    public StoreListResponseDto toStoreListResponseDto(Store store, double distance) {
        return StoreListResponseDto.builder()
                .storeId(store.getId())
                .name(store.getName())
                .openingHours(
                        store.getOpeningHours() != null ? store.getOpeningHours().toString() : null)
                .closingHours(
                        store.getClosingHours() != null ? store.getClosingHours().toString() : null)
                .storeImage(store.getStoreImage())
                .reviewCount(0) // 리뷰 미구현 → 0
                .distance(distance)
                .build();
    }

    // 가게 상세 정보 조회 응답
    public StoreDetailResponseDto toStoreDetailResponseDto(Store store) {
        return StoreDetailResponseDto.builder()
                .storeId(store.getId())
                .name(store.getName())
                .reviewCount(0) // 리뷰 미구현 → 0
                .categoryName(
                        store.getCategory() != null
                                ? store.getCategory().getType().getDisplayName()
                                : null)
                .merchantNumber(store.getMerchantNumber())
                .address(store.getRegion() != null ? store.getRegion().getAddress() : null)
                .detailAddress(
                        store.getRegion() != null ? store.getRegion().getDetailAddress() : null)
                .storeImage(store.getStoreImage())
                .openingHours(
                        store.getOpeningHours() != null ? store.getOpeningHours().toString() : null)
                .closingHours(
                        store.getClosingHours() != null ? store.getClosingHours().toString() : null)
                .build();
    }
}
