package com.dungi.core.domain.vote.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class VoteUserDetail {
    private Long userId;
    private String profileImg;
    private String nickname;
    private VoteUserChoice voteUserChoice;

    @Builder
    @Getter
    @AllArgsConstructor
    public static class VoteUserChoice {
        private String choice;
        private Long voteItemId;
    }
}
