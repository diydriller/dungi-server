package com.dungi.apiserver.application.user.dto;

import com.dungi.common.value.SnsProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor
public class CreateSnsUserDto {
    private String email;
    private String nickname;
    private String snsImg;
    private String accessToken;
    private MultipartFile profileImg;
    private SnsProvider serviceType;
}
