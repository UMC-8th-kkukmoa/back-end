package kkukmoa.kkukmoa.store.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.store.dto.response.StoreListResponseDto;
import kkukmoa.kkukmoa.store.dto.response.StorePagingResponseDto;
import kkukmoa.kkukmoa.store.service.StoreLikeService;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "가게찜 API", description = "가게 찜, 찜 조회 관련 API")
@RequestMapping("/v1/stores/like")
public class StoreLikeController {

    private final StoreLikeService storeLikeService;

    @PostMapping("/{storeId}")
    @Operation(summary = "가게 찜", description = "해당 가게를 찜합니다. 이미 찜 상태여도 성공으로 처리합니다.")
    public ResponseEntity<ApiResponse<Boolean>> like(
            @AuthenticationPrincipal User user, @PathVariable Long storeId) {
        boolean liked = storeLikeService.like(user.getUserId(), storeId);
        return ResponseEntity.ok(ApiResponse.onSuccess(liked));
    }

    @DeleteMapping("/{storeId}")
    @Operation(summary = "가게 찜 해제", description = "해당 가게의 찜을 해제합니다. 존재하지 않아도 성공으로 처리합니다.")
    public ResponseEntity<ApiResponse<Boolean>> unlike(
            @AuthenticationPrincipal User user, @PathVariable Long storeId) {
        boolean liked = storeLikeService.unlike(user.getUserId(), storeId);
        return ResponseEntity.ok(ApiResponse.onSuccess(liked));
    }

    @GetMapping("/{storeId}/count")
    @Operation(summary = "가게 찜 수", description = "특정 가게가 받은 찜 개수를 반환합니다.")
    public ResponseEntity<ApiResponse<Long>> likeCount(@PathVariable Long storeId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(storeLikeService.likeCount(storeId)));
    }

    @GetMapping("/{storeId}/me")
    @Operation(summary = "내가 이 가게를 찜했는지 여부", description = "true/false 반환")
    public ResponseEntity<ApiResponse<Boolean>> isMyLike(
            @AuthenticationPrincipal User user, @PathVariable Long storeId) {
        boolean liked = storeLikeService.isLiked(user.getUserId(), storeId);
        return ResponseEntity.ok(ApiResponse.onSuccess(liked));
    }

    @GetMapping("/users/me/likes")
    @Operation(
            summary = "내가 찜한 가게 목록(거리순)",
            description = "반경 제한 없이 전부 조회 후, 현재 위치 기준 거리 오름차순 정렬. categoryType이 있으면 해당 카테고리만 필터링.")
    public ResponseEntity<ApiResponse<StorePagingResponseDto<StoreListResponseDto>>> myLikes(
            @AuthenticationPrincipal User user,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) CategoryType categoryType) {
        var res =
                storeLikeService.getMyLikedStores(
                        user.getUserId(), latitude, longitude, page, size, categoryType);
        return ResponseEntity.ok(ApiResponse.onSuccess(res));
    }
}
