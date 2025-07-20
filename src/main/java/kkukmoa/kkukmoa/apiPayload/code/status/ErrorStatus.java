package kkukmoa.kkukmoa.apiPayload.code.status;

import kkukmoa.kkukmoa.apiPayload.code.BaseErrorCode;
import kkukmoa.kkukmoa.apiPayload.code.ErrorReasonDto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4004", "유저를 찾을 수 없습니다."),

    // 인증 관련 에러
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH4010", "인증에 실패했습니다."),

    // S3 관련 에러
    IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "S3_4001", "업로드 된 이미지가 존재하지 않습니다.."),
    NO_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "S3_4002", "파일 확장자가 존재하지 않습니다."),
    INVALID_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "S3_4003", "허용되지 않은 확장자입니다."),
    FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "FILE4004", "업로드 가능한 최대 용량을 초과했습니다."),
    INVALID_URL_FORMAT(HttpStatus.BAD_REQUEST, "S3_4004", "잘못된 이미지 URL 형식입니다."), // ← ✅ 추가된 항목
    IO_EXCEPTION_ON_IMAGE_UPLOAD(
            HttpStatus.INTERNAL_SERVER_ERROR, "S3_5001", "이미지 업로드 중 오류가 발생했습니다."),
    IO_EXCEPTION_ON_IMAGE_DELETE(
            HttpStatus.INTERNAL_SERVER_ERROR, "S3_5002", "이미지 삭제 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder().message(message).code(code).isSuccess(false).build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}
