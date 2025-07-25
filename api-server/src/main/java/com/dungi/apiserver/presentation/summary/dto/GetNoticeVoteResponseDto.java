package com.dungi.apiserver.presentation.summary.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetNoticeVoteResponseDto {

    private Long id;
    private String profileImg;
    private String notice;
    private Long userId;
    private String title;
    private String isOwner;
    private String isNotice;
    private String createdAt;
}
