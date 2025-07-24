package com.dungi.storage.store.room;

import com.dungi.common.dto.PageDto;
import com.dungi.common.exception.BaseException;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.core.domain.common.query.UserDetail;
import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.room.model.Room;
import com.dungi.core.domain.room.model.UserRoom;
import com.dungi.core.domain.user.model.User;
import com.dungi.core.integration.store.room.RoomStore;
import com.dungi.storage.dto.room.GetRoomUserDto;
import com.dungi.storage.rdb.repository.room.RoomJpaRepository;
import com.dungi.storage.rdb.repository.room.UserRoomJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoomStoreImpl implements RoomStore {
    private final UserRoomJpaRepository userRoomJpaRepository;
    private final RoomJpaRepository roomJpaRepository;

    @Override
    public Room getRoomEnteredByUser(Long userId, Long roomId) {
        return userRoomJpaRepository.getRoomEnteredByUser(userId, roomId, DeleteStatus.NOT_DELETED)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_EXIST_USER_ROOM));
    }

    @Override
    public Room saveRoom(Room room) {
        return roomJpaRepository.save(room);
    }

    @Override
    public void saveUserRoom(UserRoom userRoom) {
        userRoomJpaRepository.save(userRoom);
    }

    @Override
    public Optional<UserRoom> getUserRoomByDeleteStatus(Long userId, Room room, DeleteStatus status) {
        return userRoomJpaRepository.getUserRoom(userId, room, status);
    }

    @Override
    public Room getRoom(Long roomId) {
        return roomJpaRepository.getRoom(roomId, DeleteStatus.NOT_DELETED)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_EXIST_ROOM));
    }

    @Override
    public Integer countUserRoom(Room room) {
        return userRoomJpaRepository.countRoomMember(room, DeleteStatus.NOT_DELETED);
    }

    @Override
    public List<Room> getAllRoomEnteredByUser(PageDto dto) {
        var pageRequest = PageRequest.of(dto.getPage(), dto.getSize(), Sort.Direction.DESC, "createdTime");
        return roomJpaRepository.getAllRoomEnteredByUser(
                dto.getUserId(),
                DeleteStatus.NOT_DELETED,
                pageRequest
        );
    }

    @Override
    public int getRoomMemberCnt(Long roomId) {
        return roomJpaRepository.getRoomMemberCnt(roomId, DeleteStatus.NOT_DELETED);
    }

    @Override
    public List<UserDetail> getAllMemberInfo(Room room) {
        return roomJpaRepository.getAllMemberInfo(room).stream()
                .map(GetRoomUserDto::createRoomUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getAllMemberInRoom(Room room) {
        return userRoomJpaRepository.findAllMemberByRoomId(room.getId(), DeleteStatus.NOT_DELETED);
    }
}
