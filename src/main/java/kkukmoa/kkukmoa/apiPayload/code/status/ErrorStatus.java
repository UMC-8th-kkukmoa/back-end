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

    // 사장님 관련 에러
    OWNER_NOT_FOUND(HttpStatus.NOT_FOUND, "OWNER4004", "올바르지 않은 사장님 정보입니다."),
    OWNER_INVALID_SCAN(HttpStatus.BAD_REQUEST, "OWNER4005", "사장님은 스탬프 적립 QR을 스캔할 수 없습니다."),
    OWNER_STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "OWNER4006", "사장님 소유의 가게가 없습니다."),

    // 인증 관련 에러
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH4010", "인증에 실패했습니다."),

    // QR 코드 관련 에러
    QR_CANNOT_GENERATION(HttpStatus.BAD_REQUEST, "QR400", "QR 코드 생성에 실패했습니다."),
    QR_INVALID(HttpStatus.NOT_FOUND, "QR401", "유효하지 않는 QR 코드입니다."),

    // 쿠폰 관련 에러
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "COUPON400", "존재하지 않는 쿠폰입니다."),
    COUPON_INVALID_USED_PLACE(HttpStatus.BAD_REQUEST, "COUPON401", "올바르지 않은 사용처(사장)입니다."),
    COUPON_IS_USED(HttpStatus.BAD_REQUEST, "COUPON402", "이미 사용한 쿠폰입니다."),
    ;

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
