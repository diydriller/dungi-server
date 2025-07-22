package com.dungi.apiserver.presentation.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
public class CheckCodeRequestDto {

    @NotEmpty(message = "code is empty")
    @Pattern(regexp = "\\d{4}",message = "code format is incorrect")
    private String code;

    @NotEmpty(message = "phoneNumber is emtpy ")
    @Pattern(regexp = "\\d{11}",message = "phoneNumber format is incorrect")
    private String phoneNumber;
}
