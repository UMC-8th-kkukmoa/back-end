package kkukmoa.kkukmoa.owner.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.UserHandler;
import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.repository.CategoryRepository;
import kkukmoa.kkukmoa.owner.dto.OwnerRegisterRequest;
import kkukmoa.kkukmoa.region.domain.Region;
import kkukmoa.kkukmoa.region.service.RegionService;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.enums.StoreStatus;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.enums.UserType;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerRegisterService {
    
    // 입점 신청 서비스

    // 기존 StoreRegistrationRepository 삭제 → 단일 Store로 전환
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final RegionService regionService; // ※ Region 로직은 그대로 유지

    @Transactional
    public void applyStoreRegistration(User user, OwnerRegisterRequest request) {
        /* 1) 중복 신청 방지 정책
         */
        if (storeRepository.existsByOwner(user)) {
            throw new UserHandler(ErrorStatus.OWNER_REQUEST_ALREADY_SUBMITTED);
        }

        /* 2) 카테고리 조회 (요청 displayName → enum → 엔티티) */
        Category category =
                categoryRepository
                        .findByType(request.getCategory())
                        .orElseThrow(() -> new UserHandler(ErrorStatus.STORE_CATEGORY_NOT_FOUND));

        /* 3) Region 연동 */
        Region region =
                regionService.createRegion(
                        request.getStoreAddress(),
                        request.getStoreAddressDetail(),
                        request.getLatitude(),
                        request.getLongitude());

        Store store =
                Store.builder()
                        .owner(user) // 신청자
                        .name(request.getStoreName()) // 매장명
                        .number(request.getStorePhoneNumber()) // 대표 전화
                        .storeImage(request.getStoreImageUrl()) // 이미지 URL
                        .openingHours(request.getOpeningHours())
                        .closingHours(request.getClosingHours())
                        .qrUrl(null) // 필요 시 추후 세팅
                        .region(region) // Region 연동(현 구조 유지)
                        .category(category)
                        .status(StoreStatus.PENDING) // 신청 = PENDING
                        .build();

        storeRepository.save(store);

        /* 5) 신청자 롤 갱신 (대기 상태를 표현)
         */
        if (!user.getRoles().contains(UserType.PENDING_OWNER)) {
            user.getRoles().add(UserType.PENDING_OWNER);
        }
    }
}
