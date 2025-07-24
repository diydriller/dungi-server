package com.dungi.apiserver.presentation.room.dto;

import com.dungi.apiserver.application.room.dto.CreateRoomDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class CreateRoomRequestDto {

    @NotEmpty(message = "color is empty")
    private String color;

    @NotEmpty(message = "name is empty")
    @Size(max = 10, message = "name's max length is 10")
    private String name;

    public CreateRoomDto createRoomDto() {
        return CreateRoomDto.builder()
                .color(color)
                .name(name)
                .build();
    }
}
