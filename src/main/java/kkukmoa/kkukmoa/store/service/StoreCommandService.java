package kkukmoa.kkukmoa.store.service;

import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.category.repository.CategoryRepository;
import kkukmoa.kkukmoa.region.domain.Region;
import kkukmoa.kkukmoa.region.service.RegionCommandService;
import kkukmoa.kkukmoa.store.converter.StoreConverter;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.dto.request.StoreRequestDto;
import kkukmoa.kkukmoa.store.dto.response.StoreIdResponseDto;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class StoreCommandService {

    private final StoreRepository storeRepository;
    private final StoreConverter storeConverter;
    private final RegionCommandService regionCommandService;
    private final CategoryRepository categoryRepository;

    private final Random random = new Random();

    public StoreIdResponseDto createStore(StoreRequestDto request) {

        Region region =
                regionCommandService.createRegion(
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
}
