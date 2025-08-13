package kkukmoa.kkukmoa.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.UserHandler;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.TokenResponseDto;
import kkukmoa.kkukmoa.user.dto.TokenWithRolesResponseDto;
import kkukmoa.kkukmoa.user.enums.UserType;
import kkukmoa.kkukmoa.user.repository.RefreshTokenRepository;
import kkukmoa.kkukmoa.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    private Key key;

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 15 * 24;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 30;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public TokenResponseDto createToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        Date now = new Date();
        List<String> roles = extractRoles(user);

        String accessToken =
                Jwts.builder()
                        .setClaims(claims)
                        .claim("roles", roles)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();

        String refreshToken =
                Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                        .claim("random", UUID.randomUUID().toString())
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();

        long expiration = getExpiration(refreshToken);
        refreshTokenRepository.saveToken(user.getId(), refreshToken, expiration);

        return TokenResponseDto.of(accessToken, refreshToken);
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims =
                    Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Token expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT Token: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Malformed JWT Token: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("Invalid JWT signature: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        }
        return false;
    }

    public String createAccessToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                .claim("random", UUID.randomUUID().toString())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration =
                    Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody()
                            .getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public String getSubjectFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Authentication getAuthentication(String token) {
        String email = getSubjectFromToken(token); // sub에서 email 꺼냄
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    public long getExpiration(String token) {
        try {
            Claims claims =
                    Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            return claims.getExpiration().getTime() - System.currentTimeMillis();
        } catch (ExpiredJwtException e) {
            return 0;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT Token");
        }
    }

    public TokenWithRolesResponseDto createTokenWithRoles(User user) {
        // subject는 가급적 userId 기반 권장 (이메일 변경 이슈 방지). 기존 스타일을 그대로 쓰고 싶다면 이메일 유지도 가능.
        Claims claims = Jwts.claims().setSubject(String.valueOf(user.getId()));
        Date now = new Date();

        List<String> roles = extractRoles(user); // ["ROLE_OWNER", "ROLE_USER"] 형태

        String accessToken =
                Jwts.builder()
                        .setClaims(claims)
                        .claim("uid", user.getId()) // 식별자/조회 편의용
                        .claim("email", user.getEmail()) // 참고용(권한 판단은 DB 기준)
                        .claim("roles", roles) // ★ 액세스 토큰에 roles 포함
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();

        String refreshToken =
                Jwts.builder()
                        .setClaims(claims)
                        .claim("rand", UUID.randomUUID().toString()) // 재사용 방지용 랜덤 값
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();

        long expiration = getExpiration(refreshToken); // 남은 만료 ms
        refreshTokenRepository.saveToken(user.getId(), refreshToken, expiration); // RT는 Redis 등에 저장

        return TokenWithRolesResponseDto.of(accessToken, refreshToken, roles);
    }

    /** UserType → "ROLE_*" 문자열 리스트로 변환 */
    private List<String> extractRoles(User user) {
        return user.getRoles().stream()
                .map(UserType::getRoleName) // 예: UserType.OWNER -> "ROLE_OWNER"
                .distinct()
                .toList();
    }
}
