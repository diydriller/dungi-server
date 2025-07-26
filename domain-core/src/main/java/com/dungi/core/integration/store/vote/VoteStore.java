package com.dungi.core.integration.store.vote;

import com.dungi.core.domain.vote.model.UserVoteItem;
import com.dungi.core.domain.vote.model.Vote;
import com.dungi.core.domain.vote.model.VoteItem;
import com.dungi.core.domain.vote.query.VoteUserDetail;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteStore {
    Vote saveVote(Vote vote, List<VoteItem> voteItemList);

    Vote getVote(Long voteId);

    List<VoteItem> getVoteItemList(Vote vote);

    List<VoteUserDetail> getVoteUser(List<VoteItem> voteItemList);

    Optional<UserVoteItem> getVoteChoice(Long userId, VoteItem voteItem);

    VoteItem getVoteItem(Long choiceId, Long voteId);

    void saveUserVoteChoice(UserVoteItem userVoteItem);
}
