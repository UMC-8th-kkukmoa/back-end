package kkukmoa.kkukmoa.admin.service;

import kkukmoa.kkukmoa.admin.dto.PendingStoreSummary;
import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.AdminHandler;
import kkukmoa.kkukmoa.owner.dto.response.OwnerRegisterResponse;
import kkukmoa.kkukmoa.store.converter.StoreConverter;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.enums.StoreStatus;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminQueryService {

    private final StoreRepository storeRepository;
    private final StoreConverter storeConverter;

    public Page<PendingStoreSummary> listPending(int page, int size) {
        int safeSize = (size <= 0) ? 9 : Math.min(size, 50);
        Pageable pageable = PageRequest.of(page, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return storeRepository.findByStatus(StoreStatus.PENDING, pageable);
    }

    public OwnerRegisterResponse getPendingDetail(Long storeId) {
        Store store = storeRepository.findByIdAndStatus(storeId, StoreStatus.PENDING)
                .orElseThrow(() -> new AdminHandler(ErrorStatus.STORE_PENDING_NOT_FOUND));

        return storeConverter.toAdminResponse(store);
    }
}
