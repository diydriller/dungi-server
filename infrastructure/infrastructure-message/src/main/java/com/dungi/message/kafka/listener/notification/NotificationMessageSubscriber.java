package com.dungi.message.kafka.listener.notification;

import com.dungi.core.domain.notification.model.Notification;
import com.dungi.core.domain.notification.query.NotificationDetail;
import com.dungi.core.integration.store.notification.NotificationStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMessageSubscriber {
    private final NotificationStore notificationStore;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher applicationEventPublisher;


    @KafkaListener(topics = "notification", groupId = "notification-group")
    public void notify(String message) throws JsonProcessingException {
        NotificationDetail notificationDetail = objectMapper.readValue(message, NotificationDetail.class);
        var notification = Notification.builder()
                .senderId(notificationDetail.getSenderId())
                .receiverId(notificationDetail.getReceiverId())
                .type(notificationDetail.getType())
                .build();
        notificationStore.saveNotification(notification);
        applicationEventPublisher.publishEvent(notificationDetail);
    }
}
