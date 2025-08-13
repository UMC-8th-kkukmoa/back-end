package kkukmoa.kkukmoa.user.service;

import kkukmoa.kkukmoa.user.dto.SignupRequestDto;

public interface RegistrationService {
    void signup(SignupRequestDto req);
}
