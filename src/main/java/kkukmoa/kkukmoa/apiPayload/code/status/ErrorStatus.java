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
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON4001", "입력값이 유효하지 않습니다."),
    //사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4004", "유저를 찾을 수 없습니다."),

    // 인증 관련 에러
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH4010", "인증에 실패했습니다."),

    // 결제 관련 에러
    PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "PAYMENT4001", "결제 정보가 존재하지 않습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAYMENT4002", "결제 금액이 일치하지 않습니다."),
    PAYMENT_CONFIRM_RESPONSE_NULL(HttpStatus.BAD_REQUEST, "PAY4002", "결제 승인 응답이 비어 있습니다."),
    PAYMENT_ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "PAYMENT4003", "이미 승인된 결제입니다."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT4004", "결제 승인이 실패했습니다."),
    PAYMENT_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT4040", "결제 정보가 존재하지 않습니다."),
    PAYMENT_REDIS_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT5001", "결제 정보 저장에 실패했습니다."),
    INVALID_PAYMENT_REQUEST(HttpStatus.BAD_REQUEST, "PAYMENT4005", "유효하지 않은 결제 승인 요청입니다.");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}

