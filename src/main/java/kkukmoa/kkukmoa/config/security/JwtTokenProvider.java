package kkukmoa.kkukmoa.config.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.handler.UserHandler;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.dto.TokenResponseDto;
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

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 15;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 30;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public TokenResponseDto createToken(User user) {
        // Claims claims = Jwts.claims().setSubject(user.getEmail());
        Claims claims = Jwts.claims().setSubject(String.valueOf(user.getId()));
        Date now = new Date();

        String accessToken =
                Jwts.builder()
                        .setClaims(claims)
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

    /*    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); 수정
    }*/

    public String getSubjectFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /*    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> new UserHandler(ErrorStatus.USER_NOT_FOUND)); // 이걸 한 번만 조회

        // 여기서 User를 Principal로 넣는다!
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }*/
    public Authentication getAuthentication(String token) {
        String userIdString = getSubjectFromToken(token); // sub에서 userId 꺼냄
        Long userId = Long.parseLong(userIdString);

        User user =
                userRepository
                        .findById(userId)
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
}
