package com.dungi.apiserver.application.vote.dto;

import com.dungi.core.domain.vote.query.VoteUserDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class VoteItemInfo {

    private String title;
    private List<VoteItemInfo.VoteChoiceDto> choice;
    private List<Long> choiceIdList;
    private Boolean isOwner;
    private Boolean isFinished;
    private Integer unVotedMemberCnt;

    @Getter
    @AllArgsConstructor
    public static class VoteChoiceDto{
        private String choice;
        private List<VoteUserDetail> voteUser;
    }
}
