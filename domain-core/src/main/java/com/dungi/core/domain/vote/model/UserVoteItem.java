package com.dungi.core.domain.vote.model;

import com.dungi.common.exception.BaseException;
import com.dungi.core.domain.common.model.BaseEntity;
import com.dungi.core.domain.common.value.DeleteStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.dungi.common.response.BaseResponseStatus.INVALID_VALUE;


@Getter
@Entity
@Table(
        name = "user_vote_item",
        indexes = @Index(
                name = "user_vote_item_idx",
                columnList = "users_id, vote_item_id",
                unique = true)
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserVoteItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="users_vote_item_id")
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="vote_item_id")
    private VoteItem voteItem;

    @Column(name="users_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name="delete_status")
    private DeleteStatus deleteStatus;

    public UserVoteItem(Long userId, VoteItem voteItem){
        if(userId == null) throw new BaseException(INVALID_VALUE);
        if(voteItem == null) throw new BaseException(INVALID_VALUE);

        this.userId = userId;
        setVoteItem(voteItem);
        this.deleteStatus = DeleteStatus.NOT_DELETED;
    }

    private void setVoteItem(VoteItem voteItem){
        this.voteItem = voteItem;
        voteItem.getUserVoteItemList().add(this);
    }

    public void changeChoice(){
        if(this.deleteStatus == DeleteStatus.DELETED){
            this.deleteStatus = DeleteStatus.NOT_DELETED;
            return;
        }
        this.deleteStatus = DeleteStatus.DELETED;
    }
}
