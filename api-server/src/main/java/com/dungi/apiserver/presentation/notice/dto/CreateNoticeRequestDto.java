package com.dungi.apiserver.presentation.notice.dto;

import com.dungi.apiserver.application.notice.dto.CreateNoticeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
