package kkukmoa.kkukmoa.store.service;

import jakarta.annotation.Nullable;

import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.store.dto.response.StoreListResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StorePagingResponseDto;

import java.util.Collection;
import java.util.Set;

public interface StoreLikeService {

    boolean like(Long userId, Long storeId);

    boolean unlike(Long userId, Long storeId);

    StorePagingResponseDto<StoreListResponseDto> getMyLikedStores(
            Long userId,
            double latitude,
            double longitude,
            int page,
            int size,
            @Nullable CategoryType categoryType);

    boolean isLiked(Long userId, Long storeId);

    Set<Long> likedStoreIdsIn(Long userId, Collection<Long> storeIds);

    long likeCount(Long storeId);
}
