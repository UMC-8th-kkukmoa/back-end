package kkukmoa.kkukmoa.config.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** STOMP 기반 구조 사용 시 사용. 현재는 순수 WebSocket 기반 사용하니 해제합니다. 추후 필요 시 사용할 수 있도록 주석 처리 */
@Slf4j
// @Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();

    //  @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String email = (String) accessor.getSessionAttributes().get("email");

        if (email != null) {
            userSessionMap.put(email, sessionId);
            log.info("[+] WebSocket connected: email = {}, sessionId = {}", email, sessionId);
        } else {
            log.info("[+] WebSocket connected: no email found, sessionId = {}", sessionId);
        }
    }

    //  @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        userSessionMap.values().removeIf(sessionId::equals);
        log.info("[+] WebSocket disconnected: sessionId = {}", sessionId);
    }

    public String getSessionIdByEmail(String email) {
        return userSessionMap.get(email);
    }
}
