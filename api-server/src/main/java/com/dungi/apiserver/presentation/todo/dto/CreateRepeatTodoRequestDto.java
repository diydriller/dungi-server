package com.dungi.apiserver.presentation.todo.dto;

import com.dungi.apiserver.application.todo.dto.CreateRepeatTodoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class CreateRepeatTodoRequestDto {

    @NotEmpty(message = "todo is empty")
    @Size(max = 20, message = "todo's max length is 20")
    private String todo;

    @NotEmpty(message = "time is empty")
    @Pattern(regexp = "\\d{1,2}/\\d{1,2}", message = "time format is wrong")
    private String time;

    @NotEmpty(message = "days is empty")
    @Pattern(regexp = "[0,1]{7}", message = "time format is wrong")
    private String days;

    public CreateRepeatTodoDto createRepeatTodoDto() {
        return CreateRepeatTodoDto.builder()
                .todo(todo)
                .time(time)
                .days(days)
                .build();
    }
}
