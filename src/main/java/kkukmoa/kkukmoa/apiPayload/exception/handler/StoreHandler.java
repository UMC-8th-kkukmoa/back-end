package kkukmoa.kkukmoa.apiPayload.exception.handler;

import kkukmoa.kkukmoa.apiPayload.code.BaseErrorCode;
import kkukmoa.kkukmoa.apiPayload.exception.GeneralException;

public class StoreHandler extends GeneralException {

    public StoreHandler(BaseErrorCode code) {
        super(code);
    }
}
