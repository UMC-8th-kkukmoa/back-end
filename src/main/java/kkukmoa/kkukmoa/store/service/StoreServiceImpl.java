package kkukmoa.kkukmoa.store.service;

import jakarta.persistence.EntityNotFoundException;
import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.category.repository.CategoryRepository;
import kkukmoa.kkukmoa.common.util.s3.service.S3ImageService;
import kkukmoa.kkukmoa.region.converter.RegionConverter;
import kkukmoa.kkukmoa.region.domain.Region;
import kkukmoa.kkukmoa.store.converter.StoreConverter;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.dto.response.StoreDetailResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StoreIdResponseDto;
import kkukmoa.kkukmoa.store.dto.request.StoreRequestDto;
import kkukmoa.kkukmoa.store.dto.response.StoreListResponseDto;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final StoreConverter storeConverter;
    private final RegionConverter regionConverter;
    private final CategoryRepository categoryRepository;
    private final S3ImageService s3ImageService;
    private final Random random = new Random();

    @Override
    public StoreIdResponseDto createStore(StoreRequestDto request, MultipartFile storeImage) {

        Region region = regionConverter.toRegion(request.getAddress(), request.getDetailAddress(),
                request.getLatitude(), request.getLongitude());

        CategoryType categoryType = CategoryType.fromDisplayName(request.getCategory());
        Category category = categoryRepository.findByType(categoryType)
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

        String storeImageUrl = s3ImageService.uploadToDirectory(storeImage, "store");

        Store store = createAndSaveStore(request, region, category, storeImageUrl);

        return new StoreIdResponseDto(store.getId());
    }

    private Store createAndSaveStore(StoreRequestDto request, Region region, Category category, String storeImageUrl) {

        Store newStore = storeConverter.toStore(request, region, category);

        Store storeWithMerchantNumber = newStore.toBuilder()
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
    public List<StoreListResponseDto> getStores(double latitude, double longitude, int offset, int limit) {

        List<Store> stores = storeRepository.findAll();

        return stores.stream()
                .map(store -> storeConverter.toStoreListResponseDto(
                        store,
                        calculateDistance(latitude, longitude,
                                store.getRegion().getLatitude(),
                                store.getRegion().getLongitude())
                ))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public StoreDetailResponseDto getStoreDetail(Long storeId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found with id: " + storeId));
        return storeConverter.toStoreDetailResponseDto(store);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {

        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    public List<StoreListResponseDto> getStoresByCategory(CategoryType categoryType, double latitude, double longitude, int offset, int limit) {

        Category category = categoryRepository.findByType(categoryType)
                .orElseThrow(() -> new RuntimeException("카테고리가 DB에 등록되어 있지 않습니다."));

        List<Store> stores = storeRepository.findAllByCategory(category);

        return stores.stream()
                .map(store -> storeConverter.toStoreListResponseDto(
                        store,
                        calculateDistance(latitude, longitude,
                                store.getRegion().getLatitude(),
                                store.getRegion().getLongitude())
                ))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }
}