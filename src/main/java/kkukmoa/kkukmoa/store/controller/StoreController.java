package kkukmoa.kkukmoa.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.store.dto.response.*;
import kkukmoa.kkukmoa.store.service.StoreService;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "가게 API", description = "가게 등록, 조회 관련 API")
@RequestMapping("/v1/stores")
public class StoreController {
    private final StoreService storeService;

    //    @PostMapping
    //    @Operation(summary = "가게 등록 API", description = "가게 정보를 등록하고 가게 ID를 반환합니다.")
    //    public ApiResponse<StoreIdResponseDto> createStore(
    //            @RequestBody @Valid StoreRequestDto request) {
    //        return ApiResponse.onSuccess(storeService.createStore(request));
    //    }

    @GetMapping
    @Operation(
            summary = "가게 목록 조회 API",
            description = "현재 위치(latitude, longitude) 기준으로 가게 목록을 조회합니다.")
    @Parameters(
            value = {
                @Parameter(name = "page", description = "페이지 번호(0부터 시작)"),
                @Parameter(name = "size", description = "한 페이지 당 가게 개수")
            })
    public ApiResponse<StorePagingResponseDto<StoreListResponseDto>> getStores(
            @AuthenticationPrincipal User user,
            @RequestParam
                    @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다")
                    @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다")
                    double latitude,
            @RequestParam
                    @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다")
                    @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다")
                    double longitude,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size) {

        Long userId = (user == null) ? null : user.getUserId();

        return ApiResponse.onSuccess(
                storeService.getStores(latitude, longitude, page, size, userId));
    }

    @GetMapping("/{storeId}")
    @Operation(summary = "가게 상세 조회 API", description = "storeId로 특정 가게의 상세 정보를 조회합니다.")
    public ApiResponse<StoreDetailResponseDto> getStoreDetail(@PathVariable Long storeId) {
        return ApiResponse.onSuccess(storeService.getStoreDetail(storeId));
    }

    @GetMapping("/category")
    @Operation(
            summary = "카테고리별 가게 목록 조회 API",
            description = "카테고리명과 현재 위치 기준으로 해당 카테고리 가게 목록을 조회합니다.")
    @Parameters(
            value = {
                @Parameter(name = "page", description = "페이지 번호(0부터 시작)"),
                @Parameter(name = "size", description = "한 페이지 당 가게 개수")
            })
    public ApiResponse<StorePagingResponseDto<StoreListResponseDto>> getStoresByCategory(
            @AuthenticationPrincipal User user,
            @RequestParam CategoryType categoryType,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size) {

        Long userId = (user == null) ? null : user.getUserId();

        return ApiResponse.onSuccess(
                storeService.getStoresByCategory(
                        categoryType, latitude, longitude, page, size, userId));
    }

    @GetMapping("/search")
    @Operation(summary = "가게 검색 API", description = "가게명으로 가게를 조회합니다.")
    @Parameters(
            value = {
                @Parameter(name = "page", description = "페이지 번호(0부터 시작)"),
                @Parameter(name = "size", description = "한 페이지 당 가게 개수")
            })
    public ApiResponse<StorePagingResponseDto<StoreListResponseDto>> searchStores(
            @AuthenticationPrincipal User user,
            @RequestParam
                    @NotBlank(message = "검색어는 필수입니다.")
                    @Size(min = 1, max = 50, message = "검색어는 1~50자 이내여야 합니다.")
                    String name,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(name = "page") int page,
            @RequestParam(name = "size") int size) {

        Long userId = (user == null) ? null : user.getUserId();

        return ApiResponse.onSuccess(
                storeService.searchStoresByName(name, latitude, longitude, page, size, userId));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.onFailure("COMMON400", e.getMessage(), null));
    }
}
