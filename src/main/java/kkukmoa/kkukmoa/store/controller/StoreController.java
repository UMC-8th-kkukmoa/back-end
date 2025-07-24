package kkukmoa.kkukmoa.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.store.dto.response.StoreDetailResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StoreIdResponseDto;
import kkukmoa.kkukmoa.store.dto.request.StoreRequestDto;
import kkukmoa.kkukmoa.store.dto.response.StoreListResponseDto;
import kkukmoa.kkukmoa.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "가게 API", description = "가게 등록, 조회 관련 API")
@RequestMapping("/v1/stores")
public class StoreController {
    private final StoreService storeService;

    @PostMapping
    @Operation(summary = "가게 등록 API", description = "가게 정보를 등록하고 가게 ID를 반환합니다.")
    public ApiResponse<StoreIdResponseDto> createStore(
            @RequestPart StoreRequestDto request,
            @RequestPart MultipartFile storeImage
    ){
        return ApiResponse.onSuccess(storeService.createStore(request, storeImage));
    }

    @GetMapping
    @Operation(summary = "가게 목록 조회 API", description = "현재 위치(latitude, longitude) 기준으로 가게 목록을 조회합니다.")
    public ApiResponse<List<StoreListResponseDto>> getStores(
            @RequestParam double latitude,
            @RequestParam double longitude
    ) {
        return ApiResponse.onSuccess(storeService.getStores(latitude, longitude));
    }

    @GetMapping("/{storeId}")
    @Operation(summary = "가게 상세 조회 API", description = "storeId로 특정 가게의 상세 정보를 조회합니다.")
    public ApiResponse<StoreDetailResponseDto> getStoreDetail(
            @PathVariable Long storeId
    ) {
        return ApiResponse.onSuccess(storeService.getStoreDetail(storeId));
    }
}
