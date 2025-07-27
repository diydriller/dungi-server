package com.dungi.storage.dto.todo;

import com.dungi.core.domain.todo.query.TodoStatistic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetTodoCountDto {
    private Long userId;
    private Long todoCount;

    public static TodoStatistic createTodoStatisticInfo(GetTodoCountDto dto) {
        return TodoStatistic.builder()
                .userId(dto.getUserId())
                .todoCount(dto.getTodoCount())
                .build();
    }
}
