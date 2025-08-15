package kkukmoa.kkukmoa.common.util;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.UserHandler;
import kkukmoa.kkukmoa.config.security.JwtTokenProvider;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UserHandler(ErrorStatus.AUTHENTICATION_FAILED);
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User user)) {
            throw new UserHandler(ErrorStatus.AUTHENTICATION_FAILED);
        }

        return user;
    }

    public User getUserFromToken(String token) {
        String email = jwtTokenProvider.getSubjectFromToken(token); // 여기 사용

        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
}
