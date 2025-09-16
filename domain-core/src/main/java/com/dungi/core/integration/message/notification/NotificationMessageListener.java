package com.dungi.core.integration.message.notification;

import com.dungi.core.domain.notification.query.NotificationDetail;

public interface NotificationMessageListener {
    void sendNotificationMessage(NotificationDetail notificationDetail);
}
