package com.dungi.apiserver.presentation.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetTodayTodoResponseDto {
    private Long todoId;
    private String todo;
    private String deadline;
    private Boolean isOwner;
}
