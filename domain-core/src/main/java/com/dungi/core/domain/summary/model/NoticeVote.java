package com.dungi.core.domain.summary.model;

import com.dungi.common.exception.BaseException;
import com.dungi.core.domain.common.value.DeleteStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static com.dungi.common.response.BaseResponseStatus.INVALID_VALUE;

@Entity
@Table(name = "notice_vote")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notice_vote_id")
    private Long noticeVoteId;

    @Column(name = "users_id")
    private Long userId;

    @Column(name = "room_id")
    private Long roomId;

    private String type;

    private String content;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status")
    private DeleteStatus deleteStatus;

    @Builder
    public NoticeVote(
            Long noticeVoteId,
            Long userId,
            Long roomId,
            String type,
            String content,
            LocalDateTime createdTime
    ) {
        if(noticeVoteId == null) throw new BaseException(INVALID_VALUE);
        if(userId == null) throw new BaseException(INVALID_VALUE);
        if(roomId == null) throw new BaseException(INVALID_VALUE);
        if(type == null) throw new BaseException(INVALID_VALUE);
        if(content == null) throw new BaseException(INVALID_VALUE);
        if(createdTime == null) throw new BaseException(INVALID_VALUE);

        this.noticeVoteId = noticeVoteId;
        this.roomId = roomId;
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = createdTime;
        this.deleteStatus = DeleteStatus.NOT_DELETED;
    }
}
