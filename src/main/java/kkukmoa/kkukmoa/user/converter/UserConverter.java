package kkukmoa.kkukmoa.user.converter;

import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.TokenResponseDto;
import kkukmoa.kkukmoa.user.dto.UserResponseDto;
import kkukmoa.kkukmoa.user.enums.UserType;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserConverter {

    public UserResponseDto.loginDto toLoginDto(
            User user, boolean isNewUser, TokenResponseDto tokenResponseDto) {

        List<String> roles =
                user.getRoles().stream()
                        .map(UserType::getRoleName) // "ROLE_USER", "ROLE_ADMIN" 등
                        .toList();

        return new UserResponseDto.loginDto(
                user.getId(), tokenResponseDto, user.getEmail(), isNewUser, roles);
    }
}
