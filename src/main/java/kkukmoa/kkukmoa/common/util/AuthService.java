package kkukmoa.kkukmoa.common.util;

import kkukmoa.kkukmoa.apiPayload.exception.handler.UserHandler;
import kkukmoa.kkukmoa.user.repository.UserRepository;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
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

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication == null || authentication.getName() == null) {
            throw new UserHandler(ErrorStatus.AUTHENTICATION_FAILED);
        }

        String email = authentication.getName();
        log.info("현재 인증된 사용자 이메일: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UserHandler(ErrorStatus.AUTHENTICATION_FAILED);
        }
        return authentication.getName();
    }
}
