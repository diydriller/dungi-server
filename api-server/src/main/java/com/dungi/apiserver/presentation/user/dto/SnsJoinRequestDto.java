package com.dungi.apiserver.presentation.user.dto;

import com.dungi.apiserver.application.user.dto.CreateSnsUserDto;
import com.dungi.common.value.SnsProvider;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class SnsJoinRequestDto {

    @NotEmpty(message = "email is empty")
    @Pattern(regexp = "\\w+@\\w+.\\w+", message = "email format is wrong")
    private String email;

    @NotEmpty(message = "nickname is empty")
    @Size(max = 10, message = "nickname's max length is 10")
    private String nickname;

    private String snsImg;

    @NotEmpty(message = "token is empty")
    private String accessToken;

    private MultipartFile profileImg;

    @Pattern(regexp = "^(KAKAO)$", message = "Provider must be KAKAO")
    private String serviceType;

    public CreateSnsUserDto createSnsUserDto() {
        return CreateSnsUserDto.builder()
                .nickname(nickname)
                .profileImg(profileImg)
                .accessToken(accessToken)
                .snsImg(snsImg)
                .serviceType(SnsProvider.valueOf(serviceType))
                .email(email)
                .build();
    }
}
