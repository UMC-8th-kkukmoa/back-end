package kkukmoa.kkukmoa.region.converter;

import kkukmoa.kkukmoa.region.domain.Region;

import org.springframework.stereotype.Component;

@Component
public class RegionConverter {

    public Region toRegion(String address, String detailAddress, double latitude, double longitude) {

        // 지리적 좌표와 주소 데이터의 유효성 검증
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
      
        return Region.builder()
                .address(address)
                .detailAddress(detailAddress)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
