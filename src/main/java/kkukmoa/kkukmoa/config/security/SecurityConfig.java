package kkukmoa.kkukmoa.config.security;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() { // security를 적용하지 않을 리소스
        return web -> web.ignoring().requestMatchers("/error", "/favicon.ico");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, JwtTokenProvider jwtTokenProvider) throws Exception {
        http.formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(
                        headers ->
                                headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        request ->
                                request.requestMatchers(
                                                "/",
                                                "/home",
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**",
                                                "/v3/api-docs/**",
                                                "/v1/owners/register",
                                                "/v1/owners/login",
                                                "/v1/users/oauth/kakao",
                                                "/v1/users/reissue",
                                                "/v1/users/exchange",
                                                "/v1/users/signup/local",
                                                "/v1/users/login/local",
                                                "/v1/public/registrations/check-pending",
                                                "/ws/**",
                                                "/health", // 인프라 상태검사
                                                "/api/images/**")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/v1/stores/**")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                //                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider,
                // blacklistRepository), UsernamePasswordAuthenticationFilter.class);
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        for (String origin : allowedOrigins.split(",")) {
            configuration.addAllowedOriginPattern(origin.trim());
        }
        configuration.addAllowedOrigin("https://kkukmoa.shop");
        configuration.addAllowedOrigin("http://localhost:8081");
        configuration.addAllowedOrigin("kkukmoa://oauth");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true); // 인증 정보 포함 허용
        configuration.setMaxAge(3600L); // CORS 요청 캐싱 시간 (1시간)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
