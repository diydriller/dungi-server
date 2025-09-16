package com.dungi.core.integration.store.notification;

import com.dungi.core.domain.notification.model.Notification;

public interface NotificationStore {
    Notification saveNotification(Notification notification);
}
