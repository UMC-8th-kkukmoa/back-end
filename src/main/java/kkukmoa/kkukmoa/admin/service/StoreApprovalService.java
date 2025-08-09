package kkukmoa.kkukmoa.admin.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.UserHandler;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.enums.StoreStatus;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.enums.UserType;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class StoreApprovalService {

    private final StoreRepository storeRepository;

    @Transactional
    public void approve(Long storeId) {
        // 1) 매장 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.STORE_NOT_FOUND));

        // 2) 상태 검증: PENDING만 승인 가능
        if (store.getStatus() == StoreStatus.APPROVED) {
            // 이미 승인된 가게
            throw new UserHandler(ErrorStatus.STORE_ALREADY_APPROVED);
        }
        if (store.getStatus() != StoreStatus.PENDING) {
            // 그 외 상태(예: REJECTED)는 전환 불가
            throw new UserHandler(ErrorStatus.STORE_STATUS_INVALID_TRANSITION);
        }

        // 3) 점주 확정: 신청자 → 점주
        User owner = store.getOwner();
        if (owner == null) {
            throw new UserHandler(ErrorStatus.STORE_OWNER_NOT_FOUND);
        }
        owner.getRoles().remove(UserType.PENDING_OWNER);
        owner.getRoles().add(UserType.OWNER);

        // 4) 가맹점번호 생성 (10자리 랜덤, 중복 방지)
        String merchantNumber = createMerchantNumber();

        // 5) 상태 전이 (엔티티 규칙 메서드 사용)
        store.approve(merchantNumber, owner, store.getRegion());
        store.changeStatus(StoreStatus.APPROVED);

        // 6) 유저 역할 변경: PENDING_OWNER → OWNER
        owner.getRoles().remove(UserType.PENDING_OWNER);
        owner.getRoles().add(UserType.OWNER);
        // 영속 상태라 dirty checking으로 반영됨
    }

    /** 10자리 랜덤 + 중복 체크. 충돌 시 최대 100회 재시도 */
    private String createMerchantNumber() {
        int attempts = 0;
        final int MAX_ATTEMPTS = 100;
        String no;
        do {
            if (attempts++ >= MAX_ATTEMPTS) {
                throw new UserHandler(ErrorStatus.MERCHANT_NUMBER_GENERATION_FAILED);
            }
            no = String.format("%010d", ThreadLocalRandom.current().nextLong(10_000_000_000L));
        } while (storeRepository.findByMerchantNumber(no).isPresent());
        return no;
    }
}