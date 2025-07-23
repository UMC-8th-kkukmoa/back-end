package kkukmoa.kkukmoa.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponseDto {

    @Getter
    @AllArgsConstructor
    public static class loginDto {
        private Long id;
        private TokenResponseDto tokenResponseDto;
        private String email;
        private boolean isNewUser;
    }
}
