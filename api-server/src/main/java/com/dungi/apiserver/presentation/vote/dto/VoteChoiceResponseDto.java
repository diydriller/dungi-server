package com.dungi.apiserver.presentation.vote.dto;

import com.dungi.core.domain.vote.model.UserVoteItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteChoiceResponseDto {
    private Long userVoteItemId;
    private Long userId;
    private Long voteItemId;
    private String choice;
    private LocalDateTime createdTime;

    public static VoteChoiceResponseDto fromUserVoteItem(UserVoteItem userVoteItem) {
        return VoteChoiceResponseDto.builder()
                .userVoteItemId(userVoteItem.getId())
                .userId(userVoteItem.getUserId())
                .voteItemId(userVoteItem.getVoteItem().getId())
                .choice(userVoteItem.getVoteItem().getChoice())
                .createdTime(userVoteItem.getCreatedTime())
                .build();
    }
}
