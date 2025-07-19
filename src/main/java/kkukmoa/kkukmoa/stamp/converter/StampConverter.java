package kkukmoa.kkukmoa.stamp.converter;

import java.util.List;
import kkukmoa.kkukmoa.stamp.domain.Stamp;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto;
import kkukmoa.kkukmoa.store.domain.Store;

public class StampConverter {

  public static StampResponseDto.StampDto toStampDto(Stamp stamp, Store store) {
    return StampResponseDto.StampDto.builder()
        .id(stamp.getId())
        .storeName("준용카페")
        .stampScore(stamp.getCount())
        .build();
  }

  public static List<StampResponseDto.StampDto> toStampListDto(List<Stamp> stampList, Store store) {
     return stampList.stream().map(
        stamp -> StampConverter.toStampDto(stamp, store)
    ).toList();
  }

}
