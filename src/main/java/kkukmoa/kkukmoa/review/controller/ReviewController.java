package kkukmoa.kkukmoa.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

import kkukmoa.kkukmoa.apiPayload.exception.ApiResponse;
import kkukmoa.kkukmoa.review.dto.request.CreateReviewResponse;
import kkukmoa.kkukmoa.review.dto.response.CursorPage;
import kkukmoa.kkukmoa.review.dto.response.ReviewCardDto;
import kkukmoa.kkukmoa.review.dto.response.ReviewCursorResponse;
import kkukmoa.kkukmoa.review.service.ReviewCommandService;
import kkukmoa.kkukmoa.review.service.ReviewQueryService;
import kkukmoa.kkukmoa.user.annotation.CurrentUser;
import kkukmoa.kkukmoa.user.domain.User;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "리뷰 API", description = "리뷰 작성 시에는 사용자 정보가 필요하며, 조회 시에는 정보가 필요하지 않습니다.")
@RequestMapping("/v1/stores/{storeId}/reviews")
public class ReviewController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    @Operation(summary = "리뷰 생성 API", description = "가게에 대해 리뷰를 작성합니다. 이미지는 최대 5장까지 업로드 가능합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<CreateReviewResponse> createReview(
            @PathVariable Long storeId,
            @RequestPart(value = "content", required = false) String content,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @CurrentUser User me) {
        Long reviewId =
                reviewCommandService.createWithImages(me.getUserId(), storeId, content, images);
        return ApiResponse.onSuccess(reviewQueryService.getCreateResponse(reviewId));
    }

    @Operation(summary = "가게 리뷰 프리뷰 API", description = "상세 화면 하단에 노출할 최신 리뷰 카드 목록을 반환합니다.")
    @GetMapping("/preview")
    public ApiResponse<List<ReviewCardDto>> getPreview(
            @PathVariable Long storeId, @RequestParam(defaultValue = "2") int limit) {
        return ApiResponse.onSuccess(reviewQueryService.getPreview(storeId, limit));
    }

    @Operation(summary = "가게 리뷰 총 개수 API", description = "상세 화면의 '(리뷰 N개)' 표시에 사용됩니다.")
    @GetMapping("/count")
    public ApiResponse<Long> getCount(@PathVariable Long storeId) {
        return ApiResponse.onSuccess(reviewQueryService.countByStore(storeId));
    }

    @Operation(
            summary = "가게 리뷰 목록(커서 기반 무한스크롤) API",
            description =
                    """
                    특정 가게(storeId)에 대한 리뷰 목록을 커서 기반으로 조회합니다.
                    - 첫 요청: cursor 없이 호출
                    - 다음 요청: 응답의 nextCursor를 cursor로 전달
                    - hasNext=false면 더 이상 없음
                    """,
            parameters = {
                @Parameter(
                        name = "storeId",
                        description = "리뷰를 조회할 가게 ID",
                        required = true,
                        example = "1"),
                @Parameter(
                        name = "cursor",
                        description = "다음 페이지 조회용 커서(없으면 첫 페이지)",
                        required = false,
                        example = "MjAyNS0wOC0xMVQxNTowMDowMHw5OTk"),
                @Parameter(
                        name = "size",
                        description = "한 번에 조회할 개수",
                        required = false,
                        example = "10")
            },
            responses = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = CursorPage.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "스토어 없음",
                        content = @Content(mediaType = "application/json"))
            })
    @GetMapping("/cursor")
    public ApiResponse<ReviewCursorResponse> getByCursor(
            @PathVariable Long storeId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size) {
        // 서비스는 header가 첫 페이지에서만 채워진 ReviewCursorResponse를 반환
        return ApiResponse.onSuccess(reviewQueryService.listByCursor(storeId, cursor, size));
    }
}
