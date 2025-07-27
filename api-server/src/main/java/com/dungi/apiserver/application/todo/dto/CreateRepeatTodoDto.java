package com.dungi.apiserver.application.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateRepeatTodoDto {
    private String todo;
    private String time;
    private String days;
}
