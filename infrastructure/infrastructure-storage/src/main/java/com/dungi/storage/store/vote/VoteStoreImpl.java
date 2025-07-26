package com.dungi.storage.store.vote;

import com.dungi.common.exception.BaseException;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.vote.model.UserVoteItem;
import com.dungi.core.domain.vote.model.Vote;
import com.dungi.core.domain.vote.model.VoteItem;
import com.dungi.core.domain.vote.query.VoteUserDetail;
import com.dungi.core.integration.store.vote.VoteStore;
import com.dungi.storage.dto.vote.VoteUserDto;
import com.dungi.storage.rdb.repository.vote.UserVoteItemJpaRepository;
import com.dungi.storage.rdb.repository.vote.VoteItemJdbcRepository;
import com.dungi.storage.rdb.repository.vote.VoteItemJpaRepository;
import com.dungi.storage.rdb.repository.vote.VoteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class VoteStoreImpl implements VoteStore {
    private final VoteJpaRepository voteJpaRepository;
    private final VoteItemJpaRepository voteItemJpaRepository;
    private final UserVoteItemJpaRepository userVoteItemJpaRepository;
    private final VoteItemJdbcRepository voteItemJdbcRepository;

    @Override
    public Vote saveVote(Vote vote, List<VoteItem> voteItemList) {
        var savedVote = voteJpaRepository.save(vote);
        voteItemList.forEach(vi -> vi.setVote(savedVote));
        voteItemJdbcRepository.saveAll(voteItemList);
        return savedVote;
    }

    @Override
    public Vote getVote(Long voteId) {
        return voteJpaRepository.findByIdAndDeleteStatus(voteId, DeleteStatus.NOT_DELETED)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_EXIST_VOTE));
    }

    @Override
    public VoteItem getVoteItem(Long choiceId, Long voteId) {
        return voteItemJpaRepository.findVoteItem(choiceId, voteId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_EXIST_VOTEITEM));
    }

    @Override
    public void saveUserVoteChoice(UserVoteItem userVoteItem) {
        userVoteItemJpaRepository.save(userVoteItem);
    }

    @Override
    public List<VoteItem> getVoteItemList(Vote vote) {
        return voteItemJpaRepository.getVoteItemList(vote);
    }

    @Override
    public List<VoteUserDetail> getVoteUser(List<VoteItem> voteItemList) {
        return userVoteItemJpaRepository.getVoteUser(voteItemList, DeleteStatus.NOT_DELETED).stream()
                .map(VoteUserDto::createVoteUserItem)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserVoteItem> getVoteChoice(Long userId, VoteItem voteItem) {
        return userVoteItemJpaRepository.findByUserAndVoteItem(userId, voteItem);
    }
}
