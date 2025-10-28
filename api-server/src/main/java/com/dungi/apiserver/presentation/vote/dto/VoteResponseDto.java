package com.dungi.apiserver.presentation.vote.dto;

import com.dungi.core.domain.vote.model.Vote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteResponseDto {
    private Long voteId;
    private String title;
    private Long roomId;
    private Long userId;
    private LocalDateTime createdTime;

    public static VoteResponseDto fromVote(Vote vote) {
        return VoteResponseDto.builder()
                .voteId(vote.getId())
                .title(vote.getTitle())
                .roomId(vote.getRoomId())
                .userId(vote.getUserId())
                .createdTime(vote.getCreatedTime())
                .build();
    }
}
