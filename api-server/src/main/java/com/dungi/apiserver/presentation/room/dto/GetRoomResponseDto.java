package com.dungi.apiserver.presentation.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class GetRoomResponseDto {

    private List<RoomInfo> roomInfo;
    private String userName;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoomInfo{
        private Long roomId;
        private String roomName;
        private String roomColor;
        @Builder.Default
        private List<GetRoomUserDto> members = new ArrayList<>();
    }

    @Getter
    @AllArgsConstructor
    public static class GetRoomUserDto {
        private String profileImg;
        private String nickname;
    }
}
