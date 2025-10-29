package com.dungi.apiserver.application.room.service;

import com.dungi.apiserver.application.room.dto.CreateRoomDto;
import com.dungi.common.dto.PageDto;
import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.room.model.Room;
import com.dungi.core.domain.room.model.UserRoom;
import com.dungi.core.domain.room.query.RoomDetail;
import com.dungi.core.integration.store.room.RoomStore;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RoomService {
    private final RoomStore roomStore;

    @Transactional
    public Room createRoom(CreateRoomDto dto, Long userId) {
        var room = new Room(dto.getName(), dto.getColor());
        room.addUser(userId);
        return roomStore.saveRoom(room);
    }

    @Transactional
    public Room enterRoom(Long roomId, Long userId) {
        var room = roomStore.getRoom(roomId);
        var userRoom = roomStore.getUserRoomByDeleteStatus(userId, room, DeleteStatus.DELETED);
        userRoom.ifPresentOrElse(
                UserRoom::reenter,
                () -> {
                    room.addUser(userId);
                    roomStore.saveRoom(room);
                }
        );
        return room;
    }

    @Transactional
    public void leaveRoom(Long roomId, Long userId) {
        var room = roomStore.getRoomEnteredByUser(userId, roomId);
        var userRoom = roomStore.getUserRoomByDeleteStatus(userId, room, DeleteStatus.NOT_DELETED);
        userRoom.ifPresent(UserRoom::leave);

        var count = roomStore.countUserRoom(room);
        if (count <= 0) {
            room.deactivate();
        }
    }

    @Transactional(readOnly = true)
    public List<RoomDetail> getAllRoomInfo(PageDto dto) {
        var roomList = roomStore.getAllRoomEnteredByUser(dto);
        List<RoomDetail> roomDetailList = new ArrayList<>();
        for (var room : roomList) {
            var memberInfoList = roomStore.getAllMemberInfo(room);
            var roomInfo = RoomDetail.builder()
                    .roomId(room.getId())
                    .roomColor(room.getColor())
                    .roomName(room.getName())
                    .roomUserList(memberInfoList)
                    .build();
            roomDetailList.add(roomInfo);
        }
        return roomDetailList;
    }
}
