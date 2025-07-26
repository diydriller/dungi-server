package com.dungi.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PageDto {
    private Long roomId;
    private Long userId;
    private Integer page;
    private Integer size;
}
