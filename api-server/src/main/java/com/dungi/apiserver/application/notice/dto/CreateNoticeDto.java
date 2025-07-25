package com.dungi.apiserver.application.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateNoticeDto {
    private String noticeItem;
    private Long userId;
    private Long roomId;
}
