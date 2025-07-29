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
    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4004", "유저를 찾을 수 없습니다."),

    // 사장님 관련 에러
    OWNER_NOT_FOUND(HttpStatus.NOT_FOUND, "OWNER4004", "올바르지 않은 사장님 정보입니다."),
    OWNER_INVALID_SCAN(HttpStatus.BAD_REQUEST, "OWNER4005", "사장님은 스탬프 적립 QR을 스캔할 수 없습니다."),
    OWNER_STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "OWNER4006", "사장님 소유의 가게가 없습니다."),

    // 인증 관련 에러
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH4000", "인증에 실패했습니다."),
    // QR 코드 관련 에러
    QR_CANNOT_GENERATION(HttpStatus.BAD_REQUEST, "QR4001", "QR 코드 생성에 실패했습니다."),
    QR_INVALID(HttpStatus.NOT_FOUND, "QR4002", "유효하지 않는 QR 코드입니다."),
    QR_EXPIRED(HttpStatus.NOT_FOUND, "QR4003", "만료된 QR 코드입니다."),

    // 쿠폰 관련 에러
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "COUPON4000", "존재하지 않는 쿠폰입니다."),
    COUPON_INVALID_USED_PLACE(HttpStatus.BAD_REQUEST, "COUPON4001", "올바르지 않은 사용처(사장)입니다."),
    COUPON_IS_USED(HttpStatus.BAD_REQUEST, "COUPON4002", "이미 사용한 쿠폰입니다."),

    // S3 관련 에러
    IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "S3_4001", "업로드 된 이미지가 존재하지 않습니다.."),
    NO_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "S3_4002", "파일 확장자가 존재하지 않습니다."),
    INVALID_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "S3_4003", "허용되지 않은 확장자입니다."),
    FILE_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "FILE4004", "업로드 가능한 최대 용량을 초과했습니다."),
    INVALID_URL_FORMAT(HttpStatus.BAD_REQUEST, "S3_4004", "잘못된 이미지 URL 형식입니다."),
    IO_EXCEPTION_ON_IMAGE_UPLOAD(
            HttpStatus.INTERNAL_SERVER_ERROR, "S3_5001", "이미지 업로드 중 오류가 발생했습니다."),
    IO_EXCEPTION_ON_IMAGE_DELETE(
            HttpStatus.INTERNAL_SERVER_ERROR, "S3_5002", "이미지 삭제 중 오류가 발생했습니다."),
    // 결제 관련 에러
    PAYMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "PAYMENT4001", "결제 정보가 존재하지 않습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAYMENT4002", "결제 금액이 일치하지 않습니다."),
    PAYMENT_CONFIRM_RESPONSE_NULL(HttpStatus.BAD_REQUEST, "PAY4002", "결제 승인 응답이 비어 있습니다."),
    PAYMENT_ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "PAYMENT4003", "이미 승인된 결제입니다."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "PAYMENT4004", "결제 승인이 실패했습니다."),
    PAYMENT_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT4040", "결제 정보가 존재하지 않습니다."),
    PAYMENT_REDIS_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "PAYMENT5001", "결제 정보 저장에 실패했습니다."),
    INVALID_PAYMENT_REQUEST(HttpStatus.BAD_REQUEST, "PAYMENT4005", "유효하지 않은 결제 승인 요청입니다."),


    // 금액권 관련
    VOUCHER_NOT_FOUND(HttpStatus.NOT_FOUND, "VOUCHER4001", "금액권을 찾을 수 없습니다."),
    VOUCHER_ALREADY_USED(HttpStatus.BAD_REQUEST, "VOUCHER4002", "이미 사용된 금액권입니다."),
    VOUCHER_BALANCE_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "VOUCHER4004", "금액권 잔액이 부족합니다."),
    VOUCHER_INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "VOUCHER4001", "차감 금액은 0보다 커야 합니다."),
    // 가게 관련 에러
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE4001", "존재하지 않는 가게입니다.");

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
