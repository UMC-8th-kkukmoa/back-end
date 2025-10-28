package kkukmoa.kkukmoa.store.service;

import jakarta.annotation.Nullable;

import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.store.converter.StoreConverter;
import kkukmoa.kkukmoa.store.domain.StoreLike;
import kkukmoa.kkukmoa.store.dto.response.StoreListResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StorePagingResponseDto;
import kkukmoa.kkukmoa.store.repository.StoreLikeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreLikeQueryService {

    private final StoreLikeRepository storeLikeRepository;
    private final StoreConverter storeConverter;

    public StorePagingResponseDto<StoreListResponseDto> getMyLikedStores(
            Long userId,
            double latitude,
            double longitude,
            int page,
            int size,
            @Nullable CategoryType categoryType) {

        List<StoreLike> likes =
                (categoryType == null)
                        ? storeLikeRepository.findByUserId(userId)
                        : storeLikeRepository.findByUserIdAndCategoryType(userId, categoryType);

        List<StoreListResponseDto> items =
                likes.stream()
                        .map(StoreLike::getStore)
                        .map(
                                store -> {
                                    double d =
                                            calculateDistance(
                                                    latitude,
                                                    longitude,
                                                    store.getRegion().getLatitude(),
                                                    store.getRegion().getLongitude());
                                    return storeConverter.toStoreListResponseDto(store, d, true);
                                })
                        .sorted(Comparator.comparingDouble(StoreListResponseDto::getDistance))
                        .toList();

        int p = Math.max(0, page);
        int sz = (size <= 0) ? 10 : size;
        int from = Math.min(p * sz, items.size());
        int to = Math.min(from + sz, items.size());
        List<StoreListResponseDto> slice = items.subList(from, to);

        return StorePagingResponseDto.<StoreListResponseDto>builder()
                .stores(slice)
                .page(p)
                .totalPages((int) Math.ceil(items.size() / (double) sz))
                .totalElements(items.size())
                .isFirst(p == 0)
                .isLast(to >= items.size())
                .build();
    }

    public boolean isLiked(Long userId, Long storeId) {
        return storeLikeRepository.existsByUserIdAndStoreId(userId, storeId);
    }

    public Set<Long> likedStoreIdsIn(Long userId, Collection<Long> storeIds) {
        if (storeIds == null || storeIds.isEmpty()) return Set.of();
        return new HashSet<>(storeLikeRepository.findLikedStoreIds(userId, storeIds));
    }

    public long likeCount(Long storeId) {
        return storeLikeRepository.countByStoreId(storeId);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                                * Math.cos(Math.toRadians(lat2))
                                * Math.sin(dLon / 2)
                                * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double km = R * c;
        return Math.round(km * 100.0) / 100.0; // 소수점 2자리
    }
}
