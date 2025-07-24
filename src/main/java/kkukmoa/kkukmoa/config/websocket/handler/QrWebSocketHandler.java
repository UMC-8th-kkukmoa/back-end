package kkukmoa.kkukmoa.config.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;

import kkukmoa.kkukmoa.owner.dto.QrMessageDto;
import kkukmoa.kkukmoa.owner.dto.QrMessageDto.QrGeneralTextDto;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class QrWebSocketHandler extends TextWebSocketHandler {

    // WebSocket Session 들을 관리하는 리스트
    private static final ConcurrentHashMap<String, WebSocketSession> clientSessions =
            new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, WebSocketSession> emailSessionMap =
            new ConcurrentHashMap<>();

    // Dto -> Json 문자열로 바꾸기 위한 ObjectMapper
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * [연결 성공] WebSocket 협상이 성공적으로 완료되고 WebSocket 연결이 열려 사용할 준비가 된 후 호출됩니다. - 성공을 하였을 경우 session 값을
     * 추가합니다.
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[+] afterConnectionEstablished :: " + session.getId());
        clientSessions.put(session.getId(), session);

        String email = (String) session.getAttributes().get("email");
        if (email != null) {
            emailSessionMap.put(email, session);
            log.info("[+] afterConnectionEstablished :: " + email);
        } else {
            // TODO: 로그인 안 되어있을 때 예외처리
        }
    }

    /**
     * [메시지 전달] 새로운 WebSocket 메시지가 도착했을 때 호출됩니다. - 전달 받은 메시지를 순회하면서 메시지를 전송합니다. -
     * message.getPayload()를 통해 메시지가 전달이 됩니다.
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        log.info("[+] handleTextMessage - session :: " + session);
        log.info("[+] handleTextMessage - payload :: " + message.getPayload());

        // 예시: 수신한 메시지를 같은 이메일 가진 사용자에게만 다시 보내기
        String senderEmail = (String) session.getAttributes().get("email");
        if (senderEmail != null) {
            sendMessageToEmail(senderEmail, message.getPayload());
        }
    }

    /**
     * [소켓 종료 및 전송 오류] WebSocket 연결이 어느 쪽에서든 종료되거나 전송 오류가 발생한 후 호출됩니다. - 종료 및 실패하였을 경우 해당 세션을 제거합니다.
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
            throws Exception {

        // 세션 제거
        clientSessions.remove(session.getId()); // sessionId 기준
        emailSessionMap.values().removeIf(s -> s.getId().equals(session.getId())); // email 기준

        log.info(
                "[+] afterConnectionClosed - Session : "
                        + session.getId()
                        + " Reason : "
                        + status.getReason());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn(
                "[+] handleTransportError - session :: "
                        + session.getId()
                        + " Reason : "
                        + exception.getMessage());
    }

    public void sendMessageToEmail(String email, QrMessageDto messageDto) {
        WebSocketSession session = emailSessionMap.get(email);
        if (session != null && session.isOpen()) {
            try {
                String messageJson = objectMapper.writeValueAsString(messageDto);
                session.sendMessage(new TextMessage(messageJson));
                log.info("[+] Sent message to email: " + email);
            } catch (IOException e) {
                log.error("Failed to send message to email: " + email, e);
            }
        } else {
            log.warn("Session for email " + email + " is not open or does not exist");
        }
    }

    public void sendMessageToEmail(String email, String message) {
        QrGeneralTextDto messageDto = QrGeneralTextDto.builder().message(message).build();
        sendMessageToEmail(email, messageDto);
    }
}
