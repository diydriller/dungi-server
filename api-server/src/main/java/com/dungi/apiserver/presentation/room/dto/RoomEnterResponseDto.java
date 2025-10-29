package com.dungi.apiserver.presentation.room.dto;

import com.dungi.core.domain.room.model.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomEnterResponseDto {
    private Long roomId;
    private String roomName;
    private String roomColor;
    private Long userId;
    private LocalDateTime enteredTime;

    public static RoomEnterResponseDto fromRoom(Room room, Long userId) {
        return RoomEnterResponseDto.builder()
                .roomId(room.getId())
                .roomName(room.getName())
                .roomColor(room.getColor())
                .userId(userId)
                .enteredTime(LocalDateTime.now())
                .build();
    }
}
