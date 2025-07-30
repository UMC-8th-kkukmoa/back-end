package kkukmoa.kkukmoa.apiPayload.exception.handler;

import kkukmoa.kkukmoa.apiPayload.code.BaseErrorCode;
import kkukmoa.kkukmoa.apiPayload.exception.GeneralException;

public class VoucherHandler extends GeneralException {

    public VoucherHandler(BaseErrorCode code) {
        super(code);
    }
}
