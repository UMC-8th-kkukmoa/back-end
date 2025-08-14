package kkukmoa.kkukmoa.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerificationConfirmDto(
        @NotBlank @Email String email, @NotBlank @Pattern(regexp = "\\d{6}") String code) {}
