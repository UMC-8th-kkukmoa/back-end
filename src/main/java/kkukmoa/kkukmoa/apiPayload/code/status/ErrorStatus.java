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
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON4002", "code 누락 또는 공백 등 잘못된 파라미터입니다."),
    // 카카오 API 관련 에러
    KAKAO_API_FAILED(HttpStatus.BAD_GATEWAY, "KAKAO5001", "카카오 API 호출에 실패했습니다."),
    // 사용자 관련 에러
    DUPLICATION_DUPLICATION_EMAIL(HttpStatus.CONFLICT, "USER4003", "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4004", "유저를 찾을 수 없습니다."),
    DUPLICATION_PHONE_NUMBER(HttpStatus.CONFLICT, "USER4005", "이미 사용 중인 전화번호입니다."),
    PASSWORD_NOT_MATCH(HttpStatus.UNAUTHORIZED, "USER_401", "비밀번호가 일치하지 않습니다."),
    TERMS_NOT_AGREED(HttpStatus.BAD_REQUEST, "USER4006", "서비스 이용약관에 동의해야 합니다."),
    PRIVACY_NOT_AGREED(HttpStatus.BAD_REQUEST, "USER4007", "개인정보 처리방침에 동의해야 합니다."),
    REFRESH_TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "TOKEN4001", "Refresh Token이 필요합니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN4002", "유효하지 않은 Refresh Token입니다."),
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "TOKEN4003", "저장된 Refresh Token과 일치하지 않습니다."),
    DUPLICATION_NICKNAME(HttpStatus.CONFLICT, "U004", "이미 사용 중인 닉네임입니다."),
    // 교환코드 관련 에러
    EXCHANGE_CODE_DUPLICATE(HttpStatus.CONFLICT, "OAUTH4090", "이미 사용 중인 교환코드입니다."),
    EXCHANGE_CODE_INVALID(HttpStatus.BAD_REQUEST, "OAUTH4001", "유효하지 않거나 만료된 교환코드입니다."),
    EXCHANGE_CODE_SERIALIZE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH5001", "토큰 직렬화에 실패했습니다."),
    EXCHANGE_CODE_DESERIALIZE_FAIL(
            HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH5002", "토큰 역직렬화에 실패했습니다."),

    // 사장님 관련 에러
    OWNER_ALREADY_EXISTS(HttpStatus.CONFLICT, "OWNER4001", "이미 사장님 권한이 있습니다."),
    OWNER_REQUEST_ALREADY_SUBMITTED(HttpStatus.CONFLICT, "OWNER4002", "이미 입점 신청을 보냈습니다."),
    OWNER_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "OWNER4003", "입점 신청 이력이 없습니다."),
    OWNER_REQUEST_ALREADY_APPROVED(HttpStatus.CONFLICT, "OWNER4004", "이미 승인된 신청입니다."),
    OWNER_NOT_FOUND(HttpStatus.NOT_FOUND, "OWNER4005", "올바르지 않은 사장님 정보입니다."),
    OWNER_INVALID_SCAN(HttpStatus.BAD_REQUEST, "OWNER4006", "사장님은 스탬프 적립 QR을 스캔할 수 없습니다."),
    OWNER_STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "OWNER4007", "사장님 소유의 가게가 없습니다."),
    OWNER_CANNOT_ACCESS(HttpStatus.FORBIDDEN, "OWNER4008", "사장님만 접근할 수 있는 기능입니다."),

    // 관리자 관련 에러
    STORE_PENDING_NOT_FOUND(HttpStatus.NOT_FOUND, "ADMIN0001", "입점신청 승인 대기 중인 상태의 매장이 없습니다."),

    // 인증 관련 에러
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTH4010", "인증에 실패했습니다."),
    // QR 코드 관련 에러
    QR_CANNOT_GENERATION(HttpStatus.BAD_REQUEST, "QR4000", "QR 코드 생성에 실패했습니다."),
    QR_INVALID(HttpStatus.NOT_FOUND, "QR4001", "유효하지 않는 QR 코드입니다."),
    QR_INVALID_TYPE(HttpStatus.NOT_FOUND, "QR4002", "올바르지 않은 QR 코드 유형입니다."),

    // 쿠폰 관련 에러
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "COUPON4000", "존재하지 않는 쿠폰입니다."),
    COUPON_INVALID_USED_PLACE(HttpStatus.BAD_REQUEST, "COUPON4001", "올바르지 않은 사용처(사장)입니다."),
    COUPON_IS_USED(HttpStatus.BAD_REQUEST, "COUPON4002", "이미 사용한 쿠폰입니다."),

    // 리뷰 관련 에러
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "R404", "리뷰가 존재하지 않습니다."),
    TOO_MANY_IMAGES(HttpStatus.BAD_REQUEST, "R001", "이미지는 최대 5장까지 업로드할 수 있습니다."),

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
    VOUCHER_INVALID_AMOUNT(HttpStatus.BAD_REQUEST, "VOUCHER4005", "차감 금액은 0보다 커야 합니다."),
    // 가게 관련 에러
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE4001", "존재하지 않는 가게입니다."),
    STORE_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE4002", "존재하지 않는 가게 카테고리입니다."),

    // 상태 전환/부가
    STORE_STATUS_INVALID_TRANSITION(HttpStatus.BAD_REQUEST, "STORE4101", "현재 상태에서는 승인할 수 없습니다."),
    STORE_OWNER_NOT_FOUND(HttpStatus.BAD_REQUEST, "STORE4103", "점주 정보를 확인할 수 없습니다."),
    MERCHANT_NUMBER_GENERATION_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR, "STORE4105", "가맹점번호 생성에 실패했습니다."),

    // 입점 신청 관련 에러
    STORE_REGISTRATION_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE4003", "존재하지 않는 입점 신청입니다."),
    STORE_ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "STORE4004", "이미 승인된 입점 신청입니다."),

    // 웹소켓 관련 에러
    WEBSOCKET_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "WS4004", "인증 토큰이 필요합니다."),
    WEBSOCKET_TOKEN_INVALID(HttpStatus.BAD_REQUEST, "WS4005", "인증 토큰이 유효하지 않습니다."),
    WEBSOCKET_LOGIN_REQUIRED(HttpStatus.FORBIDDEN, "WS4006", "로그인이 필요합니다."),
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
