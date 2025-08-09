package kkukmoa.kkukmoa.user.service;

import jakarta.servlet.http.HttpServletRequest;
import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.TokenHandler;
import kkukmoa.kkukmoa.config.security.JwtTokenProvider;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.TokenResponseDto;
import kkukmoa.kkukmoa.user.repository.RefreshTokenRepository;
import kkukmoa.kkukmoa.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;


    private static final boolean ROTATE_REFRESH_TOKEN = true;


    public TokenResponseDto reissue(HttpServletRequest request) {
        // 1) 헤더에서 RT 추출
        String refreshToken = resolveRefreshToken(request);
        if (refreshToken == null) {
            throw new TokenHandler(ErrorStatus.REFRESH_TOKEN_REQUIRED);
        }

        // 2) 서명/만료 검증 (JwtTokenProvider 변경 없음)
        if (!jwtTokenProvider.validateToken(refreshToken) || jwtTokenProvider.isTokenExpired(refreshToken)) {
            throw new TokenHandler(ErrorStatus.REFRESH_TOKEN_INVALID);
        }

        // 3) Redis에서 RT → userId 확인
        Long storedUserId = refreshTokenRepository.getUserIdByToken(refreshToken);
        if (storedUserId == null) {
            throw new TokenHandler(ErrorStatus.REFRESH_TOKEN_MISMATCH);
        }

        // 4) DB에서 유저 존재 확인
        User user = userRepository.findById(storedUserId)
                .orElseThrow(() -> new TokenHandler(ErrorStatus.USER_NOT_FOUND));

        // 5) AT 재발급
        String newAccessToken = jwtTokenProvider.createAccessToken(user);

        // 6) RT 재발급 (
        String newRefreshToken = null;
        if (ROTATE_REFRESH_TOKEN) {
            // 기존 RT 삭제
            refreshTokenRepository.deleteToken(refreshToken);

            // 새 RT 발급 및 저장
            newRefreshToken = jwtTokenProvider.createRefreshToken(user);
            long rtTtlMillis = jwtTokenProvider.getExpiration(newRefreshToken);
            refreshTokenRepository.saveToken(user.getId(), newRefreshToken, rtTtlMillis);
        }

        return TokenResponseDto.of(newAccessToken, newRefreshToken);
    }

    private String resolveRefreshToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        return (auth != null && auth.startsWith("Bearer ")) ? auth.substring(7) : null;
    }
}