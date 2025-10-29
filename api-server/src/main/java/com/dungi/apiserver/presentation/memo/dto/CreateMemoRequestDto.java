package com.dungi.apiserver.presentation.memo.dto;

import com.dungi.apiserver.application.memo.dto.CreateMemoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
