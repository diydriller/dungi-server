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

    // 방 생성 기능
    // 방 저장
    @Transactional
    public Room createRoom(CreateRoomDto dto, Long userId) {
        var room = new Room(dto.getName(), dto.getColor());
        new UserRoom(userId, room);
        return roomStore.saveRoom(room);
    }

    // 방 입장 기능
    // 방 유무 확인 - 이전에 퇴장한 유저라면 재입장 or 방 입장
    @Transactional
    public void enterRoom(Long roomId, Long userId) {
        var room = roomStore.getRoom(roomId);
        roomStore.getUserRoomByDeleteStatus(userId, room, DeleteStatus.DELETED)
                .ifPresentOrElse(
                        UserRoom::reenter,
                        () -> {
                            var userRoom = new UserRoom(userId, room);
                            roomStore.saveUserRoom(userRoom);
                        }
                );
    }

    // 방 퇴장 기능
    // 방에 유저 있는지 확인 - 방에서 유저 삭제
    @Transactional
    public void leaveRoom(Long roomId, Long userId) {
        var room = roomStore.getRoomEnteredByUser(userId, roomId);
        roomStore.getUserRoomByDeleteStatus(userId, room, DeleteStatus.NOT_DELETED)
                .ifPresent(UserRoom::leave);
        var count = roomStore.countUserRoom(room);
        if (count <= 0) {
            room.deactivate();
        }
    }

    // 방 조회 기능
    // 방 조회 - 멤버 정보 조회
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
