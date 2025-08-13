package kkukmoa.kkukmoa.user.dto;

public record VerificationConfirmResponseDto(String signupToken, long expiresInSec) {
}
