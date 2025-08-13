package kkukmoa.kkukmoa.user.service;

import kkukmoa.kkukmoa.user.dto.VerificationConfirmResponseDto;

public interface VerificationService {
    void requestOtp(String email);
    VerificationConfirmResponseDto confirm(String email, String code);
}