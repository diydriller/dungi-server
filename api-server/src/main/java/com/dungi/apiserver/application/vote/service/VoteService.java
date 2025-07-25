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

import static com.dungi.common.util.StringUtil.VOTE_TYPE;

@RequiredArgsConstructor
@Service
public class VoteService {
    private final VoteStore voteStore;
    private final RoomStore roomStore;
    private final MessagePublisher messagePublisher;

    // 투표 생성 기능
    // 방에 유저 있는지 조회 - 투표 생성 - 조회용 테이블 데이터 생성
    @Transactional
    public void createVote(CreateVoteDto dto, Long userId, Long roomId) {
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
                "save-notice-vote"
        );
    }

    // 투표 조회 기능
    // 방에 유저 있는지 조회 - 투표 조회 - 투표 선택지와 투표한 사람들 조회 - 투표 안한 수 조회
    @Transactional(readOnly = true)
    public VoteItemInfo getVote(Long roomId, Long userId, Long voteId) {

        roomStore.getRoomEnteredByUser(userId, roomId);

        var vote = voteStore.getVote(voteId);

        List<VoteItemInfo.VoteChoiceDto> voteChoiceDtoList = new ArrayList<>();
        List<Long> myChoiceList = new ArrayList<>();
        Set<Long> voteUserIdSet = new HashSet<>();
        Map<String, List<VoteUserDetail>> voteUserForChoiceMap = new HashMap<>();
        var voteItemList = voteStore.getVoteItemList(vote);

        var voteUserList = voteStore.getVoteUser(voteItemList);
        for (var voteUser : voteUserList) {
            voteUserIdSet.add(voteUser.getUserId());
            if (voteUser.getUserId().equals(userId)) {
                myChoiceList.add(voteUser.getVoteUserChoice().getVoteItemId());
            }
            voteUserForChoiceMap.putIfAbsent(voteUser.getVoteUserChoice().getChoice(), new ArrayList<>());
            voteUserForChoiceMap.get(voteUser.getVoteUserChoice().getChoice()).add(voteUser);
        }
        for (var voteItem : voteItemList) {
            var choice = voteItem.getChoice();
            List<VoteUserDetail> voteUser = new ArrayList<>();
            if (voteUserForChoiceMap.containsKey(choice)) {
                voteUser = voteUserForChoiceMap.get(choice);
            }
            voteChoiceDtoList.add(
                    new VoteItemInfo.VoteChoiceDto(choice, voteUser)
            );
        }

        int memberCnt = roomStore.getRoomMemberCnt(roomId);

        return VoteItemInfo.builder()
                .title(vote.getTitle())
                .choiceIdList(myChoiceList)
                .isFinished(vote.getFinishStatus() == FinishStatus.FINISHED)
                .choice(voteChoiceDtoList)
                .isOwner(vote.getUserId().equals(userId))
                .unVotedMemberCnt(memberCnt - voteUserIdSet.size())
                .build();
    }

    // 투표 하기 기능
    // 투표 선택지 조회 - 투표하기
    @Transactional
    public void createVoteChoice(Long roomId, Long userId, Long voteId, Long choiceId) {
        roomStore.getRoomEnteredByUser(userId, roomId);
        var voteItem = voteStore.getVoteItem(choiceId, voteId);
        voteStore.getVoteChoice(userId, voteItem)
                .ifPresentOrElse(
                        (userVoteItem) -> {
                            userVoteItem.changeChoice();
                            voteStore.saveUserVoteChoice(userVoteItem);
                        }, () -> {
                            var userVoteItem = new UserVoteItem(userId, voteItem);
                            voteStore.saveUserVoteChoice(userVoteItem);
                        }
                );
    }
}
