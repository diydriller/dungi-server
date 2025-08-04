package com.dungi.apiserver.application.memo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateMemoDto {
    private String memo;
    private String memoColor;
}
