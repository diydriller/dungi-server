package com.dungi.core.domain.memo.model;


import com.dungi.core.domain.common.model.BaseEntity;
import com.dungi.core.domain.common.value.DeleteStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Getter
@Entity
@Table(name = "memo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Memo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long id;

    @Column(name = "memo_item")
    private String memoItem;

    @Column(name = "x_position")
    private Double xPosition;

    @Column(name = "y_position")
    private Double yPosition;

    @Column(name = "memo_color")
    private String memoColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status")
    private DeleteStatus deleteStatus;

    @Column(name = "users_id")
    private Long userId;

    @Column(name = "room_id")
    private Long roomId;

    @Version
    private Long version;

    @Builder
    public Memo(
            Long userId,
            Long roomId,
            String memoItem,
            Double xPosition,
            Double yPosition,
            String memoColor
    ) {
        this.userId = userId;
        this.roomId = roomId;
        this.memoItem = memoItem;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.memoColor = memoColor;
        this.deleteStatus = DeleteStatus.NOT_DELETED;
    }

    public void updateMemo(String memoItem, String memoColor) {
        this.memoItem = memoItem;
        this.memoColor = memoColor;
    }

    public void deactivate() {
        this.deleteStatus = DeleteStatus.DELETED;
    }

    public void move(double xPosition, double yPosition) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
}
