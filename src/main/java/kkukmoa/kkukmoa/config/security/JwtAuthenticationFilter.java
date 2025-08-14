package kkukmoa.kkukmoa.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveToken(request);

        try {
            if (token != null) {
                System.out.println("Token found: " + token);
                //                if (blacklistRepository.isBlacklisted(token)) {
                //                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                //                    response.getWriter().write("로그아웃된 토큰입니다.");
                //                    return;
                //                }
                if (jwtTokenProvider.validateToken(token)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println(
                            "Authentication set in SecurityContext: " + authentication.getName());
                } else {
                    System.out.println("Invalid or expired token.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid or expired token.");
                    return;
                }
            } else {
                Authentication anonymousAuth =
                        new AnonymousAuthenticationToken(
                                "anonymousUser",
                                "anonymousUser",
                                Collections.singletonList(
                                        new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
                SecurityContextHolder.getContext().setAuthentication(anonymousAuth);
            }
        } catch (Exception ex) {

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Internal server error occurred.");
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
