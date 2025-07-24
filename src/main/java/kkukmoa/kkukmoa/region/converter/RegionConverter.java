package kkukmoa.kkukmoa.region.converter;

import kkukmoa.kkukmoa.region.domain.Region;
import org.springframework.stereotype.Component;

@Component
public class RegionConverter {

    public Region toRegion(String address, String detailAddress, double latitude, double longitude) {
        return Region.builder()
                .address(address)
                .detailAddress(detailAddress)
                .latitude(latitude)
                .longitude(longitude)
                .build();

    }

}
