package kkukmoa.kkukmoa.store.service;

import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.dto.response.StoreDetailResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StoreIdResponseDto;
import kkukmoa.kkukmoa.store.dto.request.StoreRequestDto;
import kkukmoa.kkukmoa.store.dto.response.StoreListResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StoreService {
    StoreIdResponseDto createStore(StoreRequestDto request, MultipartFile storeImage);
    List<StoreListResponseDto> getStores(double latitude, double longitude, int offset, int limit);
    StoreDetailResponseDto getStoreDetail(Long storeId);
    List<StoreListResponseDto> getStoresByCategory(String categoryDisplayName, double latitude, double longitude, int offset, int limit);
}
