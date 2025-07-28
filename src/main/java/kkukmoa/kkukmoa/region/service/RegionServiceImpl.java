package kkukmoa.kkukmoa.region.service;

import kkukmoa.kkukmoa.region.converter.RegionConverter;
import kkukmoa.kkukmoa.region.domain.Region;
import kkukmoa.kkukmoa.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {
    private final RegionRepository regionRepository;
    private final RegionConverter regionConverter;

    @Override
    public Region createRegion(String address, String detailAddress, double latitude, double longitude) {
        return regionRepository.save(regionConverter.toRegion(address, detailAddress, latitude, longitude));
    }
}
