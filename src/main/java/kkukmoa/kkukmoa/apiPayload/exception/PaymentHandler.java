package kkukmoa.kkukmoa.apiPayload.exception;

import kkukmoa.kkukmoa.apiPayload.code.BaseErrorCode;

public class PaymentHandler extends GeneralException {
    public PaymentHandler(BaseErrorCode code) {
        super(code);
    }
}

