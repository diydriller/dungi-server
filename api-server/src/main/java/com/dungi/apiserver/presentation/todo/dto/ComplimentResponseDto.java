package com.dungi.apiserver.presentation.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplimentResponseDto {
    private Long senderId;
    private Long receiverId;
    private LocalDateTime complimentTime;

    public static ComplimentResponseDto of(Long senderId, Long receiverId) {
        return ComplimentResponseDto.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .complimentTime(LocalDateTime.now())
                .build();
    }
}
