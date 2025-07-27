package com.dungi.core.domain.todo.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class TodoStatistic {
    private Long userId;
    private Long todoCount;
}
