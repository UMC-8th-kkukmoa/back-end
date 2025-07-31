package kkukmoa.kkukmoa.stamp.converter;

import kkukmoa.kkukmoa.stamp.domain.Stamp;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto;
import kkukmoa.kkukmoa.store.domain.Store;

import java.util.List;

public class StampConverter {

    public static StampResponseDto.StampDto toStampDto(Stamp stamp, Store store) {
        return StampResponseDto.StampDto.builder()
                .id(stamp.getId())
                .storeName(store.getName())
                .stampScore(stamp.getCount())
                .build();
    }

    public static List<StampResponseDto.StampDto> toStampDtoList(List<Stamp> stampList) {
        System.out.println("hello");
        return stampList.stream()
                .map(stamp -> StampConverter.toStampDto(stamp, stamp.getStore()))
                .toList();
    }
}
