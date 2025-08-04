package com.dungi.apiserver.presentation.memo.dto;

import com.dungi.apiserver.application.memo.dto.UpdateMemoDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
public class UpdateMemoRequestDto {
    @NotEmpty(message = "memo is empty")
    private String memo;

    @NotEmpty(message = "color is empty")
    private String memoColor;

    public UpdateMemoDto createUpdateMemoDto() {
        return UpdateMemoDto.builder()
                .memo(memo)
                .memoColor(memoColor)
                .build();
    }
}
