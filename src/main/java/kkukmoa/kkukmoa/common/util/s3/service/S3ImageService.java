package kkukmoa.kkukmoa.common.util.s3.service;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class S3ImageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    // 1. 이미지 업로드 (디렉토리 구분 포함)
    public String uploadToDirectory(MultipartFile file, String directory) {

        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            return null;
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new GeneralException(ErrorStatus.FILE_TOO_LARGE);
        }

        validateExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + getExtension(file.getOriginalFilename());
        String key = directory + "/" + filename;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

            return s3Client.utilities().getUrl(GetUrlRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build())
                    .toExternalForm();

        } catch (IOException e) {
            throw new GeneralException(ErrorStatus.IO_EXCEPTION_ON_IMAGE_UPLOAD);
        }
    }

    // 2. 이미지 삭제
    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        String key = extractKeyFromUrl(fileUrl);
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
        } catch (S3Exception e) {
            throw new GeneralException(ErrorStatus.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    // 3. 이미지 조회 (존재 여부 확인)
    public String getImage(String imageUrl) {
        String key = extractKeyFromUrl(imageUrl);
        log.info("추출된 key: {}", key);

        HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try {
            s3Client.headObject(headObjectRequest); // 존재 여부 확인
            return s3Client.utilities().getUrl(builder ->
                    builder.bucket(bucket).key(key)).toExternalForm();
        } catch (NoSuchKeyException e) {
            log.warn("존재하지 않는 S3 key 요청: {}", key);
            throw new GeneralException(ErrorStatus.IMAGE_NOT_FOUND);
        }
    }


    // 확장자 검증
    private void validateExtension(String filename) {
        String ext = getExtension(filename).replace(".", "").toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new GeneralException(ErrorStatus.INVALID_FILE_EXTENTION);
        }
    }

    // 확장자 추출
    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf(".");
        if (lastDot == -1) {
            throw new GeneralException(ErrorStatus.NO_FILE_EXTENTION);
        }
        return filename.substring(lastDot);
    }

    // S3 URL에서 key 추출 (ex. store/uuid.jpg)
    private String extractKeyFromUrl(String url) {
        try {
            URL s3Url = new URL(url);
            return s3Url.getPath().substring(1);
        } catch (MalformedURLException e) {
            throw new GeneralException(ErrorStatus.INVALID_URL_FORMAT);
        }
    }

}
