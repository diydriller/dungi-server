package com.dungi.core.domain.notice.model;

import com.dungi.common.exception.BaseException;
import com.dungi.core.domain.common.model.BaseEntity;
import com.dungi.core.domain.common.value.DeleteStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

import static com.dungi.common.response.BaseResponseStatus.INVALID_VALUE;


@Getter
@Entity
@Table(name = "notice")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="notice_id")
    private Long id;

    @Column(name="room_id")
    private Long roomId;

    @Column(name="users_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name="delete_status")
    private DeleteStatus deleteStatus;

    @Column(name="notice_item")
    private String noticeItem;

    @Builder
    public Notice(Long roomId, Long userId, String noticeItem){
        if(StringUtils.isEmpty(noticeItem)) throw new BaseException(INVALID_VALUE);
        if(userId == null) throw new BaseException(INVALID_VALUE);
        if(roomId == null) throw new BaseException(INVALID_VALUE);

        this.roomId = roomId;
        this.userId = userId;
        this.deleteStatus = DeleteStatus.NOT_DELETED;
        this.noticeItem = noticeItem;
    }
}
