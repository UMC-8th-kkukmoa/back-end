package kkukmoa.kkukmoa.apiPayload.exception.handler;

import kkukmoa.kkukmoa.apiPayload.code.BaseErrorCode;
import kkukmoa.kkukmoa.apiPayload.exception.GeneralException;

public class QrHandler extends GeneralException {

    public QrHandler(BaseErrorCode code) {
        super(code);
    }
}
