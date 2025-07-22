package com.dungi.apiserver.presentation.user.dto;

import com.dungi.apiserver.application.user.dto.SnsLoginDto;
import com.dungi.common.value.SnsProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;


@Getter
@NoArgsConstructor
public class SnsLoginRequestDto {

    @NotEmpty(message = "email is empty")
    @Pattern(regexp = "\\w+@\\w+.\\w+", message = "email format is wrong")
    private String email;

    @NotEmpty(message = "token is empty")
    private String accessToken;

    @Pattern(regexp = "^(KAKAO)$", message = "Provider must be KAKAO")
    private String serviceType;

    public SnsLoginDto createSnsLoginDto() {
        return SnsLoginDto.builder()
                .email(email)
                .accessToken(accessToken)
                .serviceType(SnsProvider.valueOf(serviceType))
                .build();
    }
}
