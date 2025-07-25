package com.dungi.core.domain.vote.model;

import com.dungi.common.exception.BaseException;
import com.dungi.core.domain.common.model.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static com.dungi.common.response.BaseResponseStatus.INVALID_VALUE;

@Getter
@Entity
@Table(name = "vote_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="vote_item_id")
    private Long id;

    private String choice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="vote_id")
    private Vote vote;

    @OneToMany(mappedBy = "voteItem")
    private List<UserVoteItem> userVoteItemList = new ArrayList<>();

    public VoteItem(String choice){
        if(StringUtils.isEmpty(choice)) throw new BaseException(INVALID_VALUE);

        this.choice = choice;
    }

    public void setVote(Vote vote){
        this.vote = vote;
        vote.getVoteItemList().add(this);
    }
}
