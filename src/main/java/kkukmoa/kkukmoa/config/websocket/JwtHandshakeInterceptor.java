package kkukmoa.kkukmoa.config.websocket;

import kkukmoa.kkukmoa.apiPayload.code.status.ErrorStatus;
import kkukmoa.kkukmoa.apiPayload.exception.GeneralException;
import kkukmoa.kkukmoa.config.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/** WebSocketConfig에서 요청을 가로채어 요청한 사용자의 인증 정보를 추출하는 클래스입니다. 연결 전/후로 요청 정보를 로그로 출력합니다. */
@Slf4j
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes)
            throws Exception {

        log.info("[+] JwtHandshakeInterceptor beforeHandshake :: " + request.getURI());
        List<String> protocols = request.getHeaders().get("Sec-WebSocket-Protocol");
        String token = null;

        if (protocols != null && !protocols.isEmpty()) {
            token = protocols.get(0);
        }

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getSubjectFromToken(token);
            attributes.put("email", email);

        } else if (token == null) {
            throw new GeneralException(ErrorStatus.WEBSOCKET_TOKEN_NOT_FOUND);
        } else {
            throw new GeneralException(ErrorStatus.WEBSOCKET_TOKEN_INVALID);
        }

        // 서버 -> 클라이언트 subprotocol 응답 ( 꼭 필요함... )
        if (response instanceof ServerHttpResponse) {
            ((ServletServerHttpResponse) response)
                    .getServletResponse()
                    .setHeader("Sec-WebSocket-Protocol", token);
        }

        return true;
        //        log.info("[+] JwtHandshakeInterceptor beforeHandshake :: " + request.getURI());
        //        String token = extractToken(request.getHeaders().get("Authorization"));
        //        if (token != null && jwtTokenProvider.validateToken(token)) {
        //            /*            String email = jwtTokenProvider.getEmailFromToken(token);
        //            attributes.put("email", email);*/
        //            String userId = jwtTokenProvider.getSubjectFromToken(token); // sub 값
        //            attributes.put("userId", userId); // 이후 세션에서 사용
        //        } else if(token == null){
        //            throw new GeneralException(ErrorStatus.WEBSOCKET_TOKEN_NOT_FOUND);
        //        } else{
        //            throw new GeneralException(ErrorStatus.WEBSOCKET_TOKEN_INVALID);
        //        }
        //        return true;
    }

    private String extractToken(List<String> headers) {
        if (headers == null || headers.isEmpty()) return null;

        String authHeader = headers.get(0);
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        log.info("[+] JwtHandshakeInterceptor afterHandshake :: " + request.getURI());
    }
}
