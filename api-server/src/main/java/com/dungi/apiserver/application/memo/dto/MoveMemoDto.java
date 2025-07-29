package com.dungi.apiserver.application.memo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MoveMemoDto {
    private Double x;
    private Double y;
    private Long memoId;
    private Long roomId;
    private Long userId;
}
