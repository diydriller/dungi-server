package com.dungi.apiserver.presentation.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
