package kkukmoa.kkukmoa.admin.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.UserHandler;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.domain.StoreRegistration;
import kkukmoa.kkukmoa.store.enums.StoreRegistrationStatus;
import kkukmoa.kkukmoa.store.repository.StoreRegistrationRepository;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.enums.UserType;
import kkukmoa.kkukmoa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreRegistrationApprovalService {

    private final StoreRegistrationRepository storeRegistrationRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void approveStoreRegistration(Long registrationId) {
        // 1. 입점 신청 조회
        StoreRegistration registration = storeRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.STORE_REGISTRATION_NOT_FOUND));

        // 2. 중복 승인 방지
        if (registration.getStatus() == StoreRegistrationStatus.APPROVED) {
            throw new UserHandler(ErrorStatus.STORE_ALREADY_APPROVED);
        }

        // 3. 상태 변경
        registration.changeStatus(StoreRegistrationStatus.APPROVED);

        // 4. 권한 변경: PENDING_OWNER → OWNER
        User user = registration.getApplicant();
        user.getRoles().remove(UserType.PENDING_OWNER);
        user.getRoles().add(UserType.OWNER);
        userRepository.save(user);

        // 5. Store 생성
        Store store = Store.builder()
                .name(registration.getStoreName())
                .number(registration.getStorePhoneNumber())        // 전화번호
                .storeImage(registration.getStoreImageUrl())
                .openingHours(registration.getOpeningHours())
                .closingHours(registration.getClosingHours())
                .owner(user)
                .category(registration.getCategory())
                .build();

        storeRepository.save(store);
    }

}

