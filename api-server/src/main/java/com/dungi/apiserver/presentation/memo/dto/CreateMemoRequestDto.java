package com.dungi.apiserver.presentation.memo.dto;

import com.dungi.apiserver.application.memo.dto.CreateMemoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
public class CreateMemoRequestDto {

    @NotEmpty(message = "memo is empty")
    private String memo;

    @NotEmpty(message = "color is empty")
    private String memoColor;

    @Digits(integer = 2,fraction = 5)
    private double x;

    @Digits(integer = 2,fraction = 5)
    private double y;

    public CreateMemoDto createMemoDto(){
        return CreateMemoDto.builder()
                .memoItem(memo)
                .memoColor(memoColor)
                .xPosition(x)
                .yPosition(y)
                .build();
    }
}
