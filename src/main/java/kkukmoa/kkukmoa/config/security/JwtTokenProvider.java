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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

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

    /*public Authentication getAuthentication(String token) {
        String email = getSubjectFromToken(token); // sub에서 email 꺼냄
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }*/

    public Authentication getAuthentication(String token) {
        // 1. 토큰 파싱 및 클레임 추출
        Claims claims =
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

        // 2. 이메일(혹은 userId) 꺼내기
        String email = claims.get("email", String.class);
        if (email == null) {
            // 기존 방식처럼 subject를 email로 쓰는 경우
            email = claims.getSubject();
        }

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // 3. roles 클레임 꺼내기
        Object rolesObj = claims.get("roles");
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (rolesObj instanceof List<?> rolesList) {
            authorities =
                    rolesList.stream()
                            .filter(Objects::nonNull)
                            .map(Object::toString)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
        }

        // 4. Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
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
                        .claim("uid", user.getId())
                        .claim("email", user.getEmail())
                        .claim("roles", roles)
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

        long expiration = getExpiration(refreshToken);
        refreshTokenRepository.saveToken(user.getId(), refreshToken, expiration);

        return TokenWithRolesResponseDto.of(accessToken, refreshToken, roles);
    }

    /** UserType → "ROLE_*" 문자열 리스트로 변환 */
    private List<String> extractRoles(User user) {
        return user.getRoles().stream()
                .map(UserType::getRoleName) // 예: UserType.OWNER -> "ROLE_OWNER"
                .distinct()
                .toList();
    }

    public String issueSignupToken(String email, String jti, Duration ttl) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(email)
                .setId(jti)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttl.toMillis()))
                .claim("purpose", "signup")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Map<String, Object> parseClaims(String token) {
        Jws<Claims> jws = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return jws.getBody();
    }
}
