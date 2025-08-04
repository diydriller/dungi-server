package com.dungi.apiserver.application.memo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateMemoDto {
    private String memoItem;
    private String memoColor;
    private Double xPosition;
    private Double yPosition;
    private Long userId;
    private Long roomId;
}
