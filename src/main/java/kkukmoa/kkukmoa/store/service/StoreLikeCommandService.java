package kkukmoa.kkukmoa.store.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.UserHandler;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.domain.StoreLike;
import kkukmoa.kkukmoa.store.repository.StoreLikeRepository;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreLikeCommandService {

    private final StoreLikeRepository storeLikeRepository;
    private final StoreRepository storeRepository;

    public boolean like(Long userId, Long storeId) {
        if (storeLikeRepository.existsByUserIdAndStoreId(userId, storeId)) return true;

        Store store =
                storeRepository
                        .findById(storeId)
                        .orElseThrow(() -> new UserHandler(ErrorStatus.STORE_NOT_FOUND));

        try {
            storeLikeRepository.save(
                    StoreLike.builder()
                            .user(User.builder().id(userId).build())
                            .store(store)
                            .build());
        } catch (DataIntegrityViolationException ignore) {
            // 유니크 제약으로 인한 경쟁 상태 → 이미 찜된 것으로 간주
        }
        return true;
    }

    public boolean unlike(Long userId, Long storeId) {
        storeLikeRepository
                .findByUserIdAndStoreId(userId, storeId)
                .ifPresent(storeLikeRepository::delete);
        return false;
    }
}
