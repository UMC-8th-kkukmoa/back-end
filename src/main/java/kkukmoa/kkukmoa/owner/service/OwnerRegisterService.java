package kkukmoa.kkukmoa.owner.service;


import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.UserHandler;
import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.repository.CategoryRepository;
import kkukmoa.kkukmoa.owner.dto.OwnerRegisterRequest;
import kkukmoa.kkukmoa.store.domain.StoreRegistration;
import kkukmoa.kkukmoa.store.enums.StoreRegistrationStatus;
import kkukmoa.kkukmoa.store.repository.StoreRegistrationRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.enums.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class OwnerRegisterService { // 입점 신청 서비스

    private final StoreRegistrationRepository storeRegistrationRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public void applyStoreRegistration(User user, OwnerRegisterRequest request) {
        // 1. 이미 입점 신청한 이력이 있다면 예외
        if (storeRegistrationRepository.existsByApplicant(user)) {
            throw new UserHandler(ErrorStatus.OWNER_REQUEST_ALREADY_SUBMITTED);
        }

        // 2. 카테고리 조회
        Category category = categoryRepository.findByType(request.getCategory())
                .orElseThrow(() -> new UserHandler(ErrorStatus.STORE_CATEGORY_NOT_FOUND));

        // 3. StoreRegistration 생성
        StoreRegistration registration = StoreRegistration.builder()
                .applicant(user)
                .storeName(request.getStoreName())
                .storeAddress(request.getStoreAddress())
                .storeAddressDetail(request.getStoreAddressDetail())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .storePhoneNumber(request.getStorePhoneNumber())
                .storeImageUrl(request.getStoreImageUrl())
                .openingHours(request.getOpeningHours())
                .closingHours(request.getClosingHours())
                .category(category)
                .status(StoreRegistrationStatus.PENDING)
                .build();

        storeRegistrationRepository.save(registration);

        if (!user.getRoles().contains(UserType.PENDING_OWNER)) {
            user.getRoles().add(UserType.PENDING_OWNER);
            // userRepository.save(user); ← 생략 가능 (영속성 컨텍스트에서 dirty checking 됨)
        }
    }
}
