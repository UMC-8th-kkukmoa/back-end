package kkukmoa.kkukmoa.stamp.service;

import java.util.List;
import kkukmoa.kkukmoa.stamp.converter.StampConverter;
import kkukmoa.kkukmoa.stamp.domain.Stamp;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto.StampDto;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto.StampListDto;
import kkukmoa.kkukmoa.stamp.repository.StampRepository;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StampQueryService {

  private final StampRepository stampRepository;
  private final StoreRepository storeRepository;

  @Transactional(readOnly = true)
  public StampResponseDto.StampListDto stamList(String storeType) {

//    Store store = storeRepository.findByType(storeType);
    // 우선 하드코딩. store 부분 깃헙에 올라오면 수정
    Store store = storeRepository.findById(1L).orElse(null);
    List<Stamp> stampList = stampRepository.findByStore(store);

    List<StampDto> stampListDto = StampConverter.toStampListDto(stampList, store);

    return StampListDto.builder()
        .stamps(stampListDto)
        .total(stampList.size())
        .build();

  }

}
