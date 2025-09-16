package com.dungi.notificationserver.sse;

import com.dungi.core.domain.notification.query.NotificationDetail;
import com.dungi.core.integration.message.notification.NotificationMessageListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMessageListenerImpl implements NotificationMessageListener {
    private final SseEmitterService sseEmitterService;

    @Override
    public void sendNotificationMessage(NotificationDetail notificationDetail) {
        var key = notificationDetail.getReceiverId().toString();
        var emitter = sseEmitterService.findSseEmitter(key);
        sseEmitterService.send(notificationDetail, key, emitter);
    }
}
