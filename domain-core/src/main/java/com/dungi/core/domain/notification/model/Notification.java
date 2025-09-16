package com.dungi.core.domain.notification.model;

import com.dungi.core.domain.common.model.BaseEntity;
import com.dungi.core.domain.common.value.NotificationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Getter
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    public Long senderId;

    public Long receiverId;

    @Builder
    public Notification(
            Long senderId,
            Long receiverId,
            NotificationType type
    ) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
    }
}
