package com.dungi.core.domain.summary.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NoticeVoteDetail {
    private String type;
    private Long id;
    private String profileImg;
    private String content;
    private Long userId;
    private LocalDateTime createdAt;
}
