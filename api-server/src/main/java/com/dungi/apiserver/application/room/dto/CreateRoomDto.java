package com.dungi.apiserver.application.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateRoomDto {
    private String color;
    private String name;
}
