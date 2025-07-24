package com.dungi.core.integration.store.room;

import com.dungi.common.dto.PageDto;
import com.dungi.core.domain.common.query.UserDetail;
import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.room.model.Room;
import com.dungi.core.domain.room.model.UserRoom;
import com.dungi.core.domain.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomStore {
    Room getRoomEnteredByUser(Long userId, Long roomId);

    Room saveRoom(Room room);

    void saveUserRoom(UserRoom userRoom);

    Optional<UserRoom> getUserRoomByDeleteStatus(Long userId, Room room, DeleteStatus status);

    Integer countUserRoom(Room room);

    Room getRoom(Long roomId);

    List<Room> getAllRoomEnteredByUser(PageDto dto);

    int getRoomMemberCnt(Long roomId);

    List<UserDetail> getAllMemberInfo(Room room);

    List<User> getAllMemberInRoom(Room room);
}
