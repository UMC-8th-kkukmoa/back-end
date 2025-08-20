package kkukmoa.kkukmoa.store.service;

import jakarta.persistence.EntityNotFoundException;
import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.category.repository.CategoryRepository;
import kkukmoa.kkukmoa.store.converter.StoreConverter;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.dto.response.StoreDetailResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StoreListResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StorePagingResponseDto;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreQueryService {

    private final StoreRepository storeRepository;
    private final StoreConverter storeConverter;
    private final StoreLikeQueryService storeLikeQueryService;
    private final CategoryRepository categoryRepository;

    public StorePagingResponseDto<StoreListResponseDto> getStores(
            double latitude, double longitude, int page, int size, Long userId) {

        Pageable pageable = PageRequest.of(page, size);

        // 3km 내에서만 조회
        Page<Store> stores =
                storeRepository.findWithinRadiusPoint(latitude, longitude, 3000, pageable);

        List<Long> storeIds = stores.stream().map(Store::getId).toList();

        Set<Long> likedIds = storeLikeQueryService.likedStoreIdsIn(userId, storeIds);

        return storeConverter.toStorePagingResponseDto(
                stores.map(
                        store -> {
                            double d =
                                    calculateDistance(
                                            latitude,
                                            longitude,
                                            store.getRegion().getLatitude(),
                                            store.getRegion().getLongitude());
                            boolean liked = likedIds.contains(store.getId());
                            return storeConverter.toStoreListResponseDto(store, d, liked);
                        }));
    }

    public StoreDetailResponseDto getStoreDetail(Long storeId) {

        Store store =
                storeRepository
                        .findById(storeId)
                        .orElseThrow(
                                () ->
                                        new EntityNotFoundException(
                                                "Store not found with id: " + storeId));
        return storeConverter.toStoreDetailResponseDto(store);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // km 단위

        // 소수점 2자리까지만 반올림
        return Math.round(distance * 100.0) / 100.0;
    }

    public StorePagingResponseDto<StoreListResponseDto> getStoresByCategory(
            CategoryType categoryType,
            double latitude,
            double longitude,
            int page,
            int size,
            Long userId) {

        Category category =
                categoryRepository
                        .findByType(categoryType)
                        .orElseThrow(() -> new RuntimeException("카테고리가 DB에 등록되어 있지 않습니다."));

        Pageable pageable = PageRequest.of(page, size);

        Page<Store> stores =
                storeRepository.findWithinRadiusPointByCategory(
                        category.getId(), latitude, longitude, 3000, pageable);

        List<Long> storeIds = stores.stream().map(Store::getId).toList();

        Set<Long> likedIds = storeLikeQueryService.likedStoreIdsIn(userId, storeIds);

        return storeConverter.toStorePagingResponseDto(
                stores.map(
                        store -> {
                            double d =
                                    calculateDistance(
                                            latitude,
                                            longitude,
                                            store.getRegion().getLatitude(),
                                            store.getRegion().getLongitude());
                            boolean liked = likedIds.contains(store.getId());
                            return storeConverter.toStoreListResponseDto(store, d, liked);
                        }));
    }

    public StorePagingResponseDto<StoreListResponseDto> searchStoresByName(
            String name, double latitude, double longitude, int page, int size, Long userId) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("검색어를 입력하세요.");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Store> stores =
                storeRepository.findWithinRadiusPointByName(
                        name, latitude, longitude, 3000, pageable);

        List<Long> storeIds = stores.stream().map(Store::getId).toList();

        Set<Long> likedIds = storeLikeQueryService.likedStoreIdsIn(userId, storeIds);

        return storeConverter.toStorePagingResponseDto(
                stores.map(
                        store -> {
                            double d =
                                    calculateDistance(
                                            latitude,
                                            longitude,
                                            store.getRegion().getLatitude(),
                                            store.getRegion().getLongitude());
                            boolean liked = likedIds.contains(store.getId());
                            return storeConverter.toStoreListResponseDto(store, d, liked);

                        }));
    }
}