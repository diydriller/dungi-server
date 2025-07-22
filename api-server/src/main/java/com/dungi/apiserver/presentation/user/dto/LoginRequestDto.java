package com.dungi.apiserver.presentation.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotEmpty(message = "email is empty")
    @Pattern(regexp = "\\w+@\\w+.\\w+",message = "email format is wrong")
    private String email;

    @NotEmpty(message = "password is empty")
    @Size(max=10,message = "password's max length is 10")
    private String password;
}
