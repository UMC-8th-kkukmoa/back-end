package kkukmoa.kkukmoa.user.dto;

import kkukmoa.kkukmoa.user.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

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
        private final List<String> roles;
    }
}
