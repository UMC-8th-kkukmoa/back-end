package kkukmoa.kkukmoa.user.dto;

import jakarta.validation.constraints.NotBlank;
import kkukmoa.kkukmoa.user.enums.UserType;

public record SignupRequestDto(
        @NotBlank String signupToken,   // LOCAL+USER
        @NotBlank String email,
        @NotBlank String password,
        @NotBlank String nickname,
        @NotBlank UserType role
) {}
