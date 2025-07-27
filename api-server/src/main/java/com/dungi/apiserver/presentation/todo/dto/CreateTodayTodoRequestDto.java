package com.dungi.apiserver.presentation.todo.dto;

import com.dungi.apiserver.application.todo.dto.CreateTodayTodoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class CreateTodayTodoRequestDto {

    @NotEmpty(message = "todo is empty")
    @Size(max = 20, message = "todo's max length is 20")
    private String todo;

    @NotEmpty(message = "time is empty")
    @Pattern(regexp = "\\d{4}/\\d{1,2}/\\d{1,2}/\\d{1,2}/\\d{1,2}", message = "time format is wrong")
    private String time;

    public CreateTodayTodoDto createTodayTodoDto() {
        return CreateTodayTodoDto.builder()
                .todo(todo)
                .time(time)
                .build();
    }
}
