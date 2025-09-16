package com.dungi.notificationserver.presentation.notification.handler;

import com.dungi.core.domain.notification.query.NotificationDetail;
import com.dungi.core.integration.message.notification.NotificationMessageListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {
    private final NotificationMessageListener notificationMessageListener;

    @EventListener
    public void handleNotificationCreated(NotificationDetail notificationDetail) {
        notificationMessageListener.sendNotificationMessage(notificationDetail);
    }
}
