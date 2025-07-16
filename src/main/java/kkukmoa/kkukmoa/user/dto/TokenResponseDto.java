package kkukmoa.kkukmoa.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponseDto {

    private final String accessToken;
    private final String refreshToken;

    public TokenResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static TokenResponseDto of(final String accessToken, final String refreshToken) {
        return new TokenResponseDto(accessToken, refreshToken);
    }
}
