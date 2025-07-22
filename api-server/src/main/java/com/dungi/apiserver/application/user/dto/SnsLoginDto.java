package com.dungi.apiserver.application.user.dto;

import com.dungi.common.value.SnsProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SnsLoginDto {
    private String email;
    private String accessToken;
    private SnsProvider serviceType;
}
