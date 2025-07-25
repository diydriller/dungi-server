package com.dungi.core.domain.vote.model;

import com.dungi.common.exception.BaseException;
import com.dungi.core.domain.common.model.BaseEntity;
import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.common.value.FinishStatus;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.dungi.common.response.BaseResponseStatus.INVALID_VALUE;

@Getter
@Entity
@Table(name = "vote")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name="vote_status")
    private FinishStatus finishStatus;

    @OneToMany(mappedBy = "vote")
    private List<VoteItem> voteItemList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status")
    private DeleteStatus deleteStatus;

    @Column(name="users_id")
    private Long userId;

    @Column(name="room_id")
    private Long roomId;

    @Builder
    public Vote(
            Long roomId,
            Long userId,
            String title
    ){
        if(userId == null) throw new BaseException(INVALID_VALUE);
        if(roomId == null) throw new BaseException(INVALID_VALUE);
        if(StringUtils.isEmpty(title)) throw new BaseException(INVALID_VALUE);

        this.roomId = roomId;
        this.userId = userId;
        this.deleteStatus = DeleteStatus.NOT_DELETED;
        this.finishStatus = FinishStatus.UNFINISHED;
        this.title = title;
    }
}
