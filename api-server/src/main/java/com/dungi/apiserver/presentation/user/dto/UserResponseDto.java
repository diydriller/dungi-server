package com.dungi.apiserver.presentation.user.dto;

import com.dungi.core.domain.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String email;
    private String name;
    private String phoneNumber;
    private String nickname;
    private String profileImg;
    private String snsProvider;
    private LocalDateTime createdTime;

    public static UserResponseDto fromUser(User user) {
        return UserResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg())
                .snsProvider(user.getSnsProvider().name())
                .createdTime(user.getCreatedTime())
                .build();
    }
}
