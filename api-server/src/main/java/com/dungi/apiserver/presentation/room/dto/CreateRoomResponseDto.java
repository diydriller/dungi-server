package com.dungi.apiserver.presentation.room.dto;

import com.dungi.core.domain.room.model.Room;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomResponseDto {
    private Long roomId;
    private String color;
    private String name;

    public static CreateRoomResponseDto createRoomResponseDto(Room room) {
        return CreateRoomResponseDto.builder()
                .roomId(room.getId())
                .name(room.getName())
                .color(room.getColor())
                .build();
    }
}
