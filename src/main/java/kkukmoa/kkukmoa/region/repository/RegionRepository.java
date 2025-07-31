package kkukmoa.kkukmoa.region.repository;

import kkukmoa.kkukmoa.region.domain.Region;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findByAddressAndDetailAddressAndLatitudeAndLongitude(String address, String detailAddress, double latitude, double longitude);
}
