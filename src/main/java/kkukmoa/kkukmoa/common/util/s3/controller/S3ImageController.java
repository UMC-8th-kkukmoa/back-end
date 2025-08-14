package kkukmoa.kkukmoa.common.util.s3.controller;




import org.springframework.web.bind.annotation.*;

// @RestController
// @RequiredArgsConstructor
// @Slf4j
// @RequestMapping("/v1/images")
// @Tag(name = "Image API", description = "이미지 업로드 및 삭제 API")
// public class S3ImageController {
//
//    private final S3ImageService s3ImageService;
//
//    @Operation(
//            summary = "이미지 업로드 (디렉토리 구분)",
//            description = "도메인 디렉토리명을 path로 받아 Multipart 파일을 S3에 업로드하고, 업로드된 이미지의 URL을 반환합니다.",
//            responses = {
//                @ApiResponse(
//                        responseCode = "200",
//                        description = "성공적으로 이미지 업로드 완료",
//                        content =
//                                @Content(
//                                        mediaType = "text/plain",
//                                        schema =
//                                                @Schema(
//                                                        type = "string",
//                                                        example =
//
// "https://your-bucket.s3.ap-northeast-2.amazonaws.com/store/uuid.jpg"))),
//                @ApiResponse(
//                        responseCode = "400",
//                        description = "잘못된 파일(확장자 오류, 용량 초과, 비어있는 파일 등)",
//                        content = @Content(mediaType = "application/json")),
//                @ApiResponse(
//                        responseCode = "500",
//                        description = "서버 내부 오류 (업로드 처리 중 예외)",
//                        content = @Content(mediaType = "application/json"))
//            })
//    @PostMapping(value = "/upload/{directory}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> uploadImageToDirectory(
//            @Parameter(description = "도메인 디렉토리 이름 (예: store, review, qr)", required = true)
//                    @PathVariable
//                    String directory,
//            @Parameter(description = "업로드할 이미지 파일", required = true) @RequestPart(value = "image")
//                    MultipartFile file) {
//
//        String imageUrl = s3ImageService.uploadToDirectory(file, directory);
//        return ResponseEntity.ok(imageUrl);
//    }
//
//    @Operation(
//            summary = "이미지 삭제",
//            description = "S3에 업로드된 이미지의 전체 URL을 기준으로 이미지를 삭제합니다.",
//            responses = {
//                @ApiResponse(
//                        responseCode = "200",
//                        description = "이미지 삭제 완료",
//                        content =
//                                @Content(
//                                        mediaType = "text/plain",
//                                        schema =
//                                                @Schema(
//                                                        type = "string",
//                                                        example =
//                                                                "이미지 삭제 완료:"
//                                                                    + "
// https://your-bucket.s3..."))),
//                @ApiResponse(
//                        responseCode = "400",
//                        description = "URL 파싱 오류 또는 잘못된 요청",
//                        content = @Content(mediaType = "application/json")),
//                @ApiResponse(
//                        responseCode = "500",
//                        description = "삭제 처리 중 예외 발생",
//                        content = @Content(mediaType = "application/json"))
//            })
//    @DeleteMapping("/delete")
//    public ResponseEntity<String> deleteImage(
//            @Parameter(description = "삭제할 이미지의 S3 URL", required = true) @RequestParam
//                    String imageUrl) {
//        s3ImageService.delete(imageUrl);
//        return ResponseEntity.ok("이미지 삭제 완료: " + imageUrl);
//    }
//
//    @Operation(
//            summary = "단일 이미지 조회",
//            description = "S3에 업로드된 이미지의 전체 URL을 받아 해당 객체가 존재하면 그대로 반환합니다.",
//            parameters = {
//                @Parameter(
//                        name = "imageUrl",
//                        description =
//                                "전체 이미지 주소 (예:"
//                                    + "
// https://kkukmoa-static-files.s3.ap-northeast-2.amazonaws.com/review/uuid.jpg)",
//                        required = true,
//                        example =
//
// "https://kkukmoa-static-files.s3.ap-northeast-2.amazonaws.com/review/abc.jpg")
//            },
//            responses = {
//                @ApiResponse(
//                        responseCode = "200",
//                        description = "정상적으로 이미지 조회 성공",
//                        content =
//                                @Content(
//                                        mediaType = "text/plain",
//                                        schema =
//                                                @Schema(
//                                                        type = "string",
//                                                        example =
//
// "https://kkukmoa-static-files.s3.ap-northeast-2.amazonaws.com/review/abc.jpg"))),
//                @ApiResponse(
//                        responseCode = "404",
//                        description = "이미지를 찾을 수 없음 (존재하지 않는 키)",
//                        content = @Content(mediaType = "application/json")),
//                @ApiResponse(
//                        responseCode = "500",
//                        description = "서버 내부 오류 (파라미터 파싱 실패 등)",
//                        content = @Content(mediaType = "application/json"))
//            })
//    @GetMapping("/get")
//    public ResponseEntity<String> getImage(@RequestParam String imageUrl) {
//        String decodedUrl = URLDecoder.decode(imageUrl, StandardCharsets.UTF_8);
//        log.info("디코딩된 imageUrl: {}", decodedUrl);
//
//        String realImageUrl = s3ImageService.getImage(decodedUrl);
//        return ResponseEntity.ok(realImageUrl);
//    }
// }
