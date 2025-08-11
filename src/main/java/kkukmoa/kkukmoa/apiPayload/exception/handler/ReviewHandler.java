package kkukmoa.kkukmoa.apiPayload.exception.handler;

import kkukmoa.kkukmoa.apiPayload.code.BaseErrorCode;
import kkukmoa.kkukmoa.apiPayload.exception.GeneralException;

public class ReviewHandler extends GeneralException {
    public ReviewHandler(BaseErrorCode code) {
        super(code);
    }
}
