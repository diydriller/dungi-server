package com.dungi.core.domain.room.query;

import com.dungi.core.domain.common.query.UserDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Builder
@AllArgsConstructor
public class RoomDetail {
    private Long roomId;
    private String roomName;
    private String roomColor;
    @Builder.Default
    private List<UserDetail> roomUserList = new ArrayList<>();
}

