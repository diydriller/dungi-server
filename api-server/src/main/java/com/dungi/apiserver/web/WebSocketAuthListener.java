package com.dungi.apiserver.web;

import com.dungi.common.exception.AuthenticationException;
import com.dungi.common.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import static com.dungi.common.response.BaseResponseStatus.AUTHENTICATION_ERROR;

@RequiredArgsConstructor
@Component
public class WebSocketAuthListener {
    private final TokenProvider tokenProvider;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        var accessor = StompHeaderAccessor.wrap(event.getMessage());

        var authHeaders = accessor.getNativeHeader("Authorization");

        if (authHeaders == null || authHeaders.isEmpty()) {
            throw new AuthenticationException(BaseResponseStatus.AUTHENTICATION_ERROR);
        }

        String token = authHeaders.get(0).replace("Bearer ", "");

        try {
            tokenProvider.verifyToken(token);
        } catch (Exception e) {
            throw new AuthenticationException(AUTHENTICATION_ERROR);
        }
    }
}
