package kkukmoa.kkukmoa.region.service;

import kkukmoa.kkukmoa.region.domain.Region;

public interface RegionService {
    Region createRegion(String address, String detailAddress, double latitude, double longitude);
}
