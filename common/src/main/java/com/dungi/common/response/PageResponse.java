package com.dungi.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class PageResponse<T> {
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Long totalPages;
    private Boolean hasNextPage;
    private List<T> contents;
}
