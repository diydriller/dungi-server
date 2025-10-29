package com.dungi.apiserver.presentation.todo.dto;

import com.dungi.apiserver.application.todo.dto.GetRepeatTodoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepeatTodoResponseDto {
    private Long todoId;
    private String todo;
    private LocalDateTime deadline;
    private String day;
    private Long userId;

    public static RepeatTodoResponseDto fromGetRepeatTodoDto(GetRepeatTodoDto dto) {
        return RepeatTodoResponseDto.builder()
                .todoId(dto.getTodoId())
                .todo(dto.getTodo())
                .deadline(dto.getDeadline())
                .day(dto.getDay())
                .userId(dto.getUserId())
                .build();
    }
}
