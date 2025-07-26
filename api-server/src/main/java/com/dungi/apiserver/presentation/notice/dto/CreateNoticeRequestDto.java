package com.dungi.apiserver.presentation.notice.dto;

import com.dungi.apiserver.application.notice.dto.CreateNoticeDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
public class CreateNoticeRequestDto {
    @NotEmpty(message = "notice is empty")
    private String notice;

    public CreateNoticeDto createNoticeDto(Long roomId, Long userId){
        return CreateNoticeDto.builder()
                .noticeItem(notice)
                .roomId(roomId)
                .userId(userId)
                .build();
    }
}
