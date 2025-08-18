package kkukmoa.kkukmoa.store.service;

import jakarta.persistence.EntityNotFoundException;

import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.category.repository.CategoryRepository;
import kkukmoa.kkukmoa.region.domain.Region;
import kkukmoa.kkukmoa.region.service.RegionService;
import kkukmoa.kkukmoa.store.converter.StoreConverter;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.dto.request.StoreRequestDto;
import kkukmoa.kkukmoa.store.dto.response.*;
import kkukmoa.kkukmoa.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final StoreConverter storeConverter;
    private final StoreLikeService storeLikeService;
    private final RegionService regionService;
    private final CategoryRepository categoryRepository;
    private final Random random = new Random();

    @Override
    public StoreIdResponseDto createStore(StoreRequestDto request) {

        Region region =
                regionService.createRegion(
                        request.getAddress(),
                        request.getDetailAddress(),
                        request.getLatitude(),
                        request.getLongitude());

        CategoryType categoryType = CategoryType.fromDisplayName(request.getCategory());
        Category category =
                categoryRepository
                        .findByType(categoryType)
                        .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

        Store store = createAndSaveStore(request, region, category, request.getStoreImage());

        return new StoreIdResponseDto(store.getId());
    }

    private Store createAndSaveStore(
            StoreRequestDto request, Region region, Category category, String storeImageUrl) {

        Store newStore = storeConverter.toStore(request, region, category);

        Store storeWithMerchantNumber =
                newStore.toBuilder()
                        .merchantNumber(createMerchantNumber())
                        .storeImage(storeImageUrl)
                        .build();

        return storeRepository.save(storeWithMerchantNumber);
    }

    private String createMerchantNumber() {

        String merchantNumber;
        int attempts = 0;
        final int MAX_ATTEMPTS = 100;
        do {
            if (attempts++ >= MAX_ATTEMPTS) {
                throw new IllegalStateException("가맹점 번호 생성 실패: 최대 시도 횟수 초과");
            }
            merchantNumber = String.format("%010d", random.nextLong(1_000_000_0000L));
        } while (storeRepository.findByMerchantNumber(merchantNumber).isPresent());
        return merchantNumber;
    }

    @Override
    public StorePagingResponseDto<StoreListResponseDto> getStores(
            double latitude, double longitude, int page, int size, Long userId) {

        Pageable pageable = PageRequest.of(page, size);

        // 3km 내에서만 조회
        Page<Store> stores =
                storeRepository.findWithinRadiusPoint(latitude, longitude, 3000, pageable);

        List<Long> storeIds = stores.stream().map(Store::getId).toList();

        Set<Long> likedIds = storeLikeService.likedStoreIdsIn(userId, storeIds);

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

    @Override
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

    @Override
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

        Set<Long> likedIds = storeLikeService.likedStoreIdsIn(userId, storeIds);

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

    @Override
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

        Set<Long> likedIds = storeLikeService.likedStoreIdsIn(userId, storeIds);

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
