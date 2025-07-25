package kkukmoa.kkukmoa.store.service;

import jakarta.persistence.EntityNotFoundException;
import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.converter.CategoryConverter;
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
    private final CategoryConverter categoryConverter;
    private final Random random = new Random();

    @Override
    public StoreIdResponseDto createStore(StoreRequestDto request, MultipartFile storeImage) {

        Region region = regionConverter.toRegion(request.getAddress(), request.getDetailAddress(),
                request.getLatitude(), request.getLongitude());
        Category category = categoryConverter.toCategory(request.getCategory());
        Store store = createAndSaveStore(request, region, category);

        return new StoreIdResponseDto(store.getId());
    }

    private Store createAndSaveStore(StoreRequestDto request, Region region, Category category) {

        Store newStore = storeConverter.toStore(request, region, category);

        Store storeWithMerchantNumber = newStore.toBuilder()
                .merchantNumber(createMerchantNumber())
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
    public List<StoreListResponseDto> getStores(double latitude, double longitude) {
        List<Store> stores = storeRepository.findAll();
        return stores.stream()
                .map(store -> storeConverter.toStoreListResponseDto(
                        store,
                        calculateDistance(latitude, longitude,
                                store.getRegion().getLatitude(),
                                store.getRegion().getLongitude())
                ))
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

    //todo s3 추가하기.
//    public Store createAndSaveStoreImage(MultipartFile storeImage, Store store){
//
//
//    }
}