package kkukmoa.kkukmoa.apiPayload.code;

public interface BaseErrorCode {

    String getMessage();

    ErrorReasonDto getReason();

    ErrorReasonDto getReasonHttpStatus();
}
