package com.dungi.apiserver.presentation.vote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class GetVoteItemResponseDto {

    private String title;
    private List<VoteChoiceDto> choice;
    private List<Long> choiceIdList;
    private Boolean isOwner;
    private Boolean isFinished;
    private Integer unVotedMemberCnt;

    @Getter
    @AllArgsConstructor
    public static class VoteChoiceDto{
        private String choice;
        private List<VoteUserDto> voteUser;
    }

    @Getter
    @AllArgsConstructor
    public static class VoteUserDto{
        private String profileImg;
        private String nickname;
    }
}
