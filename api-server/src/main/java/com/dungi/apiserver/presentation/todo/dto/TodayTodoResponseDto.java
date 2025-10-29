package com.dungi.apiserver.presentation.todo.dto;

import com.dungi.core.domain.todo.model.TodayTodo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayTodoResponseDto {
    private Long todoId;
    private String todo;
    private LocalDateTime deadline;
    private Long userId;
    private Long roomId;

    public static TodayTodoResponseDto fromTodayTodo(TodayTodo todayTodo) {
        return TodayTodoResponseDto.builder()
                .todoId(todayTodo.getId())
                .todo(todayTodo.getTodoItem())
                .deadline(todayTodo.getDeadline())
                .userId(todayTodo.getUserId())
                .roomId(todayTodo.getRoomId())
                .build();
    }
}
