package kkukmoa.kkukmoa.review.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.ReviewHandler;
import kkukmoa.kkukmoa.common.util.s3.service.S3ImageService;
import kkukmoa.kkukmoa.review.domain.Review;
import kkukmoa.kkukmoa.review.domain.ReviewImage;
import kkukmoa.kkukmoa.review.repository.ReviewRepository;
import kkukmoa.kkukmoa.store.domain.Store;
import kkukmoa.kkukmoa.store.repository.StoreRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandService {

    private static final int MAX_IMAGES = 5;

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final S3ImageService s3ImageService;

    /**
     * [리뷰 작성 + 이미지 업로드] - 작성자 ID, 가게 ID, 내용, 이미지 파일을 받아 리뷰를 생성합니다. - 이미지가 있으면 S3에 업로드 후
     * ReviewImage로 연동합니다. - 이미지 개수 제한 및 유효성 검증 포함.
     *
     * @param userId 리뷰 작성자 ID
     * @param storeId 리뷰 대상 가게 ID
     * @param content 리뷰 내용
     * @param images 업로드할 이미지 리스트 (null 가능)
     * @return 생성된 리뷰의 ID
     */
    public Long createWithImages(
            Long userId, Long storeId, String content, List<MultipartFile> images) {
        // 1) 작성자/가게 참조
        User writer = userRepository.getReferenceById(userId);
        Store store = storeRepository.getReferenceById(storeId);

        // 2) 이미지 개수 검증(선택 파트가 null일 수 있으므로 방어)
        List<MultipartFile> files = (images == null) ? List.of() : images;
        if (files.size() > MAX_IMAGES) {
            throw new ReviewHandler(ErrorStatus.TOO_MANY_IMAGES); // 커스텀 에러코드 권장
        }

        // 3) S3 업로드 (디렉토리 규칙: reviews/{storeId}/{userId})
        String directory = "reviews/" + storeId + "/" + userId;

        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            // S3ImageService가 확장자/사이즈 검증 및 업로드 + 공개 URL 반환
            String url = s3ImageService.uploadToDirectory(file, directory);
            if (url != null) {
                uploadedUrls.add(url);
            }
        }

        // 4) Review 엔티티 생성(이미지는 생성 시점에만 add)
        Review review = Review.builder().writer(writer).store(store).content(content).build();

        for (String url : uploadedUrls) {
            review.addImageOnCreate(ReviewImage.builder().imageUrl(url).build());
        }

        // 5) 저장
        reviewRepository.save(review);
        return review.getId();
    }
}
