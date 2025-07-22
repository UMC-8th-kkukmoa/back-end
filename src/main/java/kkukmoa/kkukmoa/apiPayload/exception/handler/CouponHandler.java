package kkukmoa.kkukmoa.apiPayload.exception.handler;

import kkukmoa.kkukmoa.apiPayload.code.BaseErrorCode;
import kkukmoa.kkukmoa.apiPayload.exception.GeneralException;

public class CouponHandler extends GeneralException {

  public CouponHandler(BaseErrorCode code) {
    super(code);
  }
}
