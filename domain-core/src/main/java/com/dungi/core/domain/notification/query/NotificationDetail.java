package com.dungi.core.domain.notification.query;

import com.dungi.core.domain.common.value.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDetail {
    private Long senderId;
    private Long receiverId;
    private NotificationType type;
}
