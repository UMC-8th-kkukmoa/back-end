package kkukmoa.kkukmoa.store.service;

import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.store.dto.request.StoreRequestDto;
import kkukmoa.kkukmoa.store.dto.response.*;

public interface StoreService {
    StoreIdResponseDto createStore(StoreRequestDto request);

    StorePagingResponseDto<StoreListResponseDto> getStores(
            double latitude, double longitude, int page, int size, Long userId);

    StoreDetailResponseDto getStoreDetail(Long storeId);

    StorePagingResponseDto<StoreListResponseDto> getStoresByCategory(
            CategoryType categoryType,
            double latitude,
            double longitude,
            int page,
            int size,
            Long userId);

    StorePagingResponseDto<StoreListResponseDto> searchStoresByName(
            String name, double latitude, double longitude, int page, int size, Long userId);
}
