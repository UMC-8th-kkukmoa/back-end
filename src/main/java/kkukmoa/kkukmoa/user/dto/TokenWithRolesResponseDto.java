package kkukmoa.kkukmoa.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenWithRolesResponseDto {
    private String accessToken;        // 액세스 토큰
    private String refreshToken;       // 리프레시 토큰
    private List<String> roles;        // 예: ["ROLE_OWNER", "ROLE_USER"]

    public static TokenWithRolesResponseDto of(String accessToken, String refreshToken, List<String> roles) {
        return new TokenWithRolesResponseDto(accessToken, refreshToken, roles);
    }
}