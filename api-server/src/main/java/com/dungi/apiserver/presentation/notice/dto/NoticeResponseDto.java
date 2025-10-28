package com.dungi.apiserver.presentation.notice.dto;

import com.dungi.core.domain.notice.model.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseDto {
    private Long noticeId;
    private String noticeItem;
    private Long roomId;
    private Long userId;
    private LocalDateTime createdTime;

    public static NoticeResponseDto fromNotice(Notice notice) {
        return NoticeResponseDto.builder()
                .noticeId(notice.getId())
                .noticeItem(notice.getNoticeItem())
                .roomId(notice.getRoomId())
                .userId(notice.getUserId())
                .createdTime(notice.getCreatedTime())
                .build();
    }
}
