package com.dungi.apiserver.presentation.memo.dto;

import com.dungi.core.domain.memo.model.Memo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MoveMemoResponseDto {
    private Long id;

    private Double x;

    private Double y;

    public static MoveMemoResponseDto fromEntity(Memo memo) {
        return MoveMemoResponseDto.builder()
                .id(memo.getId())
                .x(memo.getXPosition())
                .y(memo.getYPosition())
                .build();
    }
}
