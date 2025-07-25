package com.dungi.apiserver.presentation.vote.dto;

import com.dungi.apiserver.application.vote.dto.CreateVoteDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreateVoteRequestDto {
    @NotEmpty(message = "title is empty")
    private String title;

    @Size(min = 1, message = "choiceList is empty")
    private List<@NotEmpty(message = "choice is empty") String> choiceList = new ArrayList<>();

    public CreateVoteDto createVoteDto() {
        return CreateVoteDto.builder()
                .choiceList(choiceList)
                .title(title)
                .build();
    }
}
