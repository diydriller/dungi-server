package com.dungi.apiserver.application.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor
public class CreateUserDto {
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private String nickname;
    private MultipartFile img;
}
