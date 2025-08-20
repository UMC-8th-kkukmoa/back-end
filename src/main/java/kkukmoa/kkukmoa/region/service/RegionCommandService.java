package kkukmoa.kkukmoa.region.service;

import kkukmoa.kkukmoa.region.converter.RegionConverter;
import kkukmoa.kkukmoa.region.domain.Region;
import kkukmoa.kkukmoa.region.repository.RegionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegionCommandService {

    private final RegionRepository regionRepository;
    private final RegionConverter regionConverter;

    public Region createRegion(
            String address, String detailAddress, double latitude, double longitude) {
        return regionRepository
                .findByAddressAndDetailAddressAndLatitudeAndLongitude(
                        address, detailAddress, latitude, longitude)
                .orElseGet(() -> createIfAbsent(address, detailAddress, latitude, longitude));
    }

    private Region createIfAbsent(
            String address, String detailAddress, double latitude, double longitude) {
        try {
            return regionRepository.save(
                    regionConverter.toRegion(address, detailAddress, latitude, longitude));
        } catch (DataIntegrityViolationException e) {
            return regionRepository
                    .findByAddressAndDetailAddressAndLatitudeAndLongitude(
                            address, detailAddress, latitude, longitude)
                    .orElseThrow(() -> e);
        }
    }
}
