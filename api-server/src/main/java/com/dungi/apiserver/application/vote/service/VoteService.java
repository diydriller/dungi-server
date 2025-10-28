package com.dungi.apiserver.application.vote.service;

import com.dungi.apiserver.application.vote.dto.CreateVoteDto;
import com.dungi.apiserver.application.vote.dto.VoteItemInfo;
import com.dungi.core.domain.common.value.FinishStatus;
import com.dungi.core.domain.summary.event.SaveNoticeVoteEvent;
import com.dungi.core.domain.vote.model.UserVoteItem;
import com.dungi.core.domain.vote.model.Vote;
import com.dungi.core.domain.vote.model.VoteItem;
import com.dungi.core.domain.vote.query.VoteUserDetail;
import com.dungi.core.integration.message.common.MessagePublisher;
import com.dungi.core.integration.store.room.RoomStore;
import com.dungi.core.integration.store.vote.VoteStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.dungi.common.util.StringUtil.SAVE_NOTICE_VOTE_TOPIC;
import static com.dungi.common.util.StringUtil.VOTE_TYPE;

@RequiredArgsConstructor
@Service
public class VoteService {
    private final VoteStore voteStore;
    private final RoomStore roomStore;
    private final MessagePublisher messagePublisher;

    @Transactional
    public Vote createVote(CreateVoteDto dto, Long userId, Long roomId) {
        roomStore.getRoomEnteredByUser(userId, roomId);

        var vote = Vote.builder()
                .title(dto.getTitle())
                .roomId(roomId)
                .userId(userId)
                .build();
        var voteItemList = dto.getChoiceList().stream()
                .map(VoteItem::new)
                .collect(Collectors.toList());
        var savedVote = voteStore.saveVote(vote, voteItemList);

        messagePublisher.publish(
                SaveNoticeVoteEvent.builder()
                        .content(dto.getTitle())
                        .createdTime(savedVote.getCreatedTime())
                        .userId(userId)
                        .roomId(roomId)
                        .type(VOTE_TYPE)
                        .id(savedVote.getId())
                        .build(),
                SAVE_NOTICE_VOTE_TOPIC
        );
        return savedVote;
    }

    @Transactional(readOnly = true)
    public VoteItemInfo getVote(Long roomId, Long userId, Long voteId) {

        roomStore.getRoomEnteredByUser(userId, roomId);

        var vote = voteStore.getVote(voteId);
        var voteItemList = voteStore.getVoteItemList(vote);

        var voteUserList = voteStore.getVoteUser(voteItemList);
        var voteUserData = collectVoteUserData(voteUserList, userId);
        var voteChoiceDtoList = buildChoiceDtoList(voteItemList, voteUserData.voteUserForChoiceMap);
        int memberCnt = roomStore.getRoomMemberCnt(roomId);

        return VoteItemInfo.builder()
                .title(vote.getTitle())
                .choiceIdList(voteUserData.myChoiceList)
                .isFinished(vote.getFinishStatus() == FinishStatus.FINISHED)
                .choice(voteChoiceDtoList)
                .isOwner(vote.getUserId().equals(userId))
                .unVotedMemberCnt(memberCnt - voteUserData.myChoiceList.size())
                .build();
    }

    @Transactional
    public UserVoteItem createVoteChoice(Long roomId, Long userId, Long voteId, Long choiceId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        var voteItem = voteStore.getVoteItem(choiceId, voteId);
        
        return voteStore.getVoteChoice(userId, voteItem)
                .map(userVoteItem -> {
                    userVoteItem.changeChoice();
                    voteStore.saveUserVoteChoice(userVoteItem);
                    return userVoteItem;
                })
                .orElseGet(() -> {
                    var userVoteItem = new UserVoteItem(userId, voteItem);
                    voteStore.saveUserVoteChoice(userVoteItem);
                    return userVoteItem;
                });
    }

    private static class VoteUserData {
        List<Long> myChoiceList;
        Set<Long> voteUserIdSet;
        Map<String, List<VoteUserDetail>> voteUserForChoiceMap;

        public VoteUserData(List<Long> myChoiceList, Set<Long> voteUserIdSet, Map<String, List<VoteUserDetail>> voteUserForChoiceMap) {
            this.myChoiceList = myChoiceList;
            this.voteUserIdSet = voteUserIdSet;
            this.voteUserForChoiceMap = voteUserForChoiceMap;
        }
    }

    private VoteUserData collectVoteUserData(List<VoteUserDetail> voteUserList, Long userId) {
        List<Long> myChoiceList = new ArrayList<>();
        Set<Long> voteUserIdSet = new HashSet<>();
        Map<String, List<VoteUserDetail>> voteUserForChoiceMap = new HashMap<>();

        for (var voteUser : voteUserList) {
            voteUserIdSet.add(voteUser.getUserId());

            if (voteUser.getUserId().equals(userId)) {
                myChoiceList.add(voteUser.getVoteUserChoice().getVoteItemId());
            }

            String choice = voteUser.getVoteUserChoice().getChoice();
            voteUserForChoiceMap.computeIfAbsent(choice, k -> new ArrayList<>()).add(voteUser);
        }

        return new VoteUserData(myChoiceList, voteUserIdSet, voteUserForChoiceMap);
    }

    private List<VoteItemInfo.VoteChoiceDto> buildChoiceDtoList(List<VoteItem> voteItemList, Map<String, List<VoteUserDetail>> voteUserForChoiceMap) {
        List<VoteItemInfo.VoteChoiceDto> voteChoiceDtoList = new ArrayList<>();

        for (var voteItem : voteItemList) {
            String choice = voteItem.getChoice();
            List<VoteUserDetail> voteUsers = voteUserForChoiceMap.getOrDefault(choice, List.of());
            voteChoiceDtoList.add(new VoteItemInfo.VoteChoiceDto(choice, voteUsers));
        }

        return voteChoiceDtoList;
    }
}
