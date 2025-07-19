package kkukmoa.kkukmoa.stamp.dto.stampDto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class StampResponseDto {

  @Builder
  @Getter
  public static class StampListDto {
    List<StampDto> stamps;
    Integer total;
    // TODO 스탬프 카테고리 추가하기
  }

  @Builder
  @Getter
  public static class StampDto {
    Long id;
    String storeName;
    Integer stampScore;
  }

}
