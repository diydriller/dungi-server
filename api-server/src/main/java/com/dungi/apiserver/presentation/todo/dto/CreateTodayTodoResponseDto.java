package com.dungi.apiserver.presentation.todo.dto;

import com.dungi.core.domain.todo.model.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CreateTodayTodoResponseDto {
    private Long id;
    private String todoItem;
    private LocalDateTime deadline;
}
