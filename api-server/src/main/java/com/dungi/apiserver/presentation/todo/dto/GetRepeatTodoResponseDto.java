package com.dungi.apiserver.presentation.todo.dto;

import com.dungi.apiserver.application.todo.dto.GetRepeatTodoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@AllArgsConstructor
public class GetRepeatTodoResponseDto {
    private Long todoId;
    private String todo;
    private String deadline;
    private Boolean isOwner;
    private String day;

    public static GetRepeatTodoResponseDto of(GetRepeatTodoDto todo, Long userId){
        return GetRepeatTodoResponseDto.builder()
                .todoId(todo.getTodoId())
                .todo(todo.getTodo())
                .deadline(DateTimeFormatter.ofPattern("HH/mm").format(todo.getDeadline()))
                .isOwner(todo.getUserId().equals(userId))
                .day(todo.getDay())
                .build();
    }
}
