package kkukmoa.kkukmoa.store.converter;

import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.region.domain.Region;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.dto.request.StoreRequestDto;
import kkukmoa.kkukmoa.store.dto.response.StoreDetailResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StoreListResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StoreSearchResponseDto;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

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
                .address(request.getAddress())
                .addressDetail(request.getDetailAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }

    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm");

    // 가게 목록 조회 응답
    public StoreListResponseDto toStoreListResponseDto(Store store, double distance) {
        String opening =
                store.getOpeningHours() != null ? store.getOpeningHours().format(TF) : null;
        String closing =
                store.getClosingHours() != null ? store.getClosingHours().format(TF) : null;

        String categoryName = null;
        if (store.getCategory() != null && store.getCategory().getType() != null) {
            categoryName = store.getCategory().getType().getDisplayName();
        }

        return StoreListResponseDto.builder()
                .storeId(store.getId())
                .name(store.getName())
                .openingHours(opening)
                .closingHours(closing)
                .storeImage(store.getStoreImage())
                .latitude(store.getRegion().getLatitude())
                .longitude(store.getRegion().getLongitude())
                .categoryName(categoryName)
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
                        store.getOpeningHours() != null ? store.getOpeningHours().format(TF) : null)
                .closingHours(
                        store.getClosingHours() != null ? store.getClosingHours().format(TF) : null)
                .build();
    }

    // 가게 검색 응답
    public StoreSearchResponseDto toSearchDto(Store store) {
        return StoreSearchResponseDto.builder().id(store.getId()).name(store.getName()).build();
    }
}
