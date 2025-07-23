package kkukmoa.kkukmoa.config.websocket;

import kkukmoa.kkukmoa.config.security.JwtTokenProvider;
import kkukmoa.kkukmoa.config.websocket.handler.QrWebSocketHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final QrWebSocketHandler qrWebSocketHandler;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("[+] 최초 WebSocket 연결을 위한 등록 Handler");

        registry.addHandler(qrWebSocketHandler, "/ws")
                .addInterceptors(new JwtHandshakeInterceptor(jwtTokenProvider)) // ★ interceptor 추가
                .setAllowedOrigins("*");
    }
}
