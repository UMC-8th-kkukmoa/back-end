package kkukmoa.kkukmoa.store.converter;

import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.region.domain.Region;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.dto.request.StoreRequestDto;
import kkukmoa.kkukmoa.store.dto.response.StoreDetailResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StoreListResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StorePagingResponseDto;

import org.springframework.data.domain.Page;
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
                .build();
    }

    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm");

    // 가게 목록 조회 응답
    public StoreListResponseDto toStoreListResponseDto(Store store, double distance, boolean liked) {
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
                .liked(liked)
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

    public <T> StorePagingResponseDto<T> toStorePagingResponseDto(Page<T> page) {
        return StorePagingResponseDto.<T>builder()
                .stores(page.getContent())
                .page(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements((int) page.getTotalElements())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .build();
    }
}
