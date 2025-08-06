package kkukmoa.kkukmoa.store.service;

import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.store.dto.request.StoreRequestDto;
import kkukmoa.kkukmoa.store.dto.response.StoreDetailResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StoreIdResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StoreListResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StoreSearchResponseDto;

import java.util.List;

public interface StoreService {
    StoreIdResponseDto createStore(StoreRequestDto request);

    List<StoreListResponseDto> getStores(double latitude, double longitude, int offset, int limit);

    StoreDetailResponseDto getStoreDetail(Long storeId);

    List<StoreListResponseDto> getStoresByCategory(
            CategoryType categoryType, double latitude, double longitude, int offset, int limit);

    List<StoreSearchResponseDto> searchStoresByName(String name, int offset, int limit);
}
