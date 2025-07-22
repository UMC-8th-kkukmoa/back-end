package kkukmoa.kkukmoa.apiPayload.code.status;

import kkukmoa.kkukmoa.apiPayload.code.BaseCode;
import kkukmoa.kkukmoa.apiPayload.code.ReasonDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
    _OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    IMAGE_UPLOAD_SUCCESS(HttpStatus.OK, "S3_2001", "이미지 업로드 성공"),
    IMAGE_DELETE_SUCCESS(HttpStatus.OK, "S3_2002", "이미지 삭제 성공"),
    SUCCESS_NO_CONTENT(HttpStatus.OK, "S3_2003", "이미지를 선택하지 않았습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder().message(message).code(code).isSuccess(true).build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(true)
                .httpStatus(httpStatus)
                .build();
    }
}
