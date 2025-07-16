package kkukmoa.kkukmoa.user.converter;

import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.TokenResponseDto;
import kkukmoa.kkukmoa.user.dto.UserResponseDto;

import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserResponseDto.loginDto toLoginDto(
            User user, boolean isNewUser, TokenResponseDto tokenResponseDto) {
        return new UserResponseDto.loginDto(
                user.getId(), tokenResponseDto, user.getEmail(), isNewUser);
    }
}
