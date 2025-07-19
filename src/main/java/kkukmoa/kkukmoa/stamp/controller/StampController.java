package kkukmoa.kkukmoa.stamp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto;
import kkukmoa.kkukmoa.stamp.dto.stampDto.StampResponseDto.StampListDto;
import kkukmoa.kkukmoa.stamp.service.StampQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "스탬프&쿠폰", description = "스탬프와 쿠폰에 해당하는 API 목록")
@RestController
@RequestMapping("/v1/stamps")
@RequiredArgsConstructor
public class StampController {

  private final StampQueryService stampQueryService;

  @GetMapping("/")
  @Operation(summary = "스탬프 목록 조회 API", description = "스탬프 타입을 입력하세요. 페이징 X")
  public ApiResponse<StampResponseDto.StampListDto> stamps(@RequestParam(value = "store-type") String storeType) {
    StampListDto stampList = stampQueryService.stamList(storeType);
    return ApiResponse.onSuccess(stampList);
  }



}
