package com.dungi.apiserver.application.room.service;

import com.dungi.apiserver.application.room.dto.CreateRoomDto;
import com.dungi.common.dto.PageDto;
import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.room.model.Room;
import com.dungi.core.domain.room.model.UserRoom;
import com.dungi.core.domain.room.query.RoomDetail;
import com.dungi.core.integration.store.room.RoomStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomStore roomStore;

    @InjectMocks
    private RoomService roomService;

    private final Long ROOM_ID = 1L;
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("방 생성 성공")
    void createRoom_Success() {
        // given
        CreateRoomDto dto = CreateRoomDto.builder()
                .name("테스트 방")
                .color("blue")
                .build();

        Room savedRoom = mock(Room.class);
        when(savedRoom.getId()).thenReturn(ROOM_ID);
        when(savedRoom.getName()).thenReturn("테스트 방");
        when(savedRoom.getColor()).thenReturn("blue");

        when(roomStore.saveRoom(any(Room.class))).thenReturn(savedRoom);

        // when
        Room result = roomService.createRoom(dto, USER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ROOM_ID);
        assertThat(result.getName()).isEqualTo("테스트 방");
        assertThat(result.getColor()).isEqualTo("blue");

        verify(roomStore).saveRoom(any(Room.class));
    }

    @Test
    @DisplayName("방 입장 성공 - 새로운 입장")
    void enterRoom_Success_NewEntry() {
        // given
        Room room = mock(Room.class);
        when(room.getId()).thenReturn(ROOM_ID);
        when(room.getName()).thenReturn("테스트 방");
        when(room.getColor()).thenReturn("blue");

        when(roomStore.getRoom(ROOM_ID)).thenReturn(room);
        when(roomStore.getUserRoomByDeleteStatus(USER_ID, room, DeleteStatus.DELETED))
                .thenReturn(Optional.empty());

        // when
        Room result = roomService.enterRoom(ROOM_ID, USER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ROOM_ID);
        assertThat(result.getName()).isEqualTo("테스트 방");
        assertThat(result.getColor()).isEqualTo("blue");

        verify(roomStore).getRoom(ROOM_ID);
        verify(roomStore).getUserRoomByDeleteStatus(USER_ID, room, DeleteStatus.DELETED);
        verify(room).addUser(USER_ID);
        verify(roomStore).saveRoom(room);
    }

    @Test
    @DisplayName("방 입장 성공 - 재입장")
    void enterRoom_Success_Reentry() {
        // given
        Room room = mock(Room.class);
        when(room.getId()).thenReturn(ROOM_ID);
        when(room.getName()).thenReturn("테스트 방");
        when(room.getColor()).thenReturn("blue");

        UserRoom userRoom = mock(UserRoom.class);

        when(roomStore.getRoom(ROOM_ID)).thenReturn(room);
        when(roomStore.getUserRoomByDeleteStatus(USER_ID, room, DeleteStatus.DELETED))
                .thenReturn(Optional.of(userRoom));

        // when
        Room result = roomService.enterRoom(ROOM_ID, USER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(ROOM_ID);
        assertThat(result.getName()).isEqualTo("테스트 방");
        assertThat(result.getColor()).isEqualTo("blue");

        verify(roomStore).getRoom(ROOM_ID);
        verify(roomStore).getUserRoomByDeleteStatus(USER_ID, room, DeleteStatus.DELETED);
        verify(userRoom).reenter();
        verify(room, never()).addUser(USER_ID);
        verify(roomStore, never()).saveRoom(room);
    }

    @Test
    @DisplayName("방 퇴장 성공 - 방이 비활성화되지 않음")
    void leaveRoom_Success_RoomNotDeactivated() {
        // given
        Room room = mock(Room.class);

        UserRoom userRoom = mock(UserRoom.class);

        when(roomStore.getRoomEnteredByUser(USER_ID, ROOM_ID)).thenReturn(room);
        when(roomStore.getUserRoomByDeleteStatus(USER_ID, room, DeleteStatus.NOT_DELETED))
                .thenReturn(Optional.of(userRoom));
        when(roomStore.countUserRoom(room)).thenReturn(2);

        // when
        roomService.leaveRoom(ROOM_ID, USER_ID);

        // then
        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(roomStore).getUserRoomByDeleteStatus(USER_ID, room, DeleteStatus.NOT_DELETED);
        verify(userRoom).leave();
        verify(roomStore).countUserRoom(room);
        verify(room, never()).deactivate();
    }

    @Test
    @DisplayName("방 퇴장 성공 - 방이 비활성화됨")
    void leaveRoom_Success_RoomDeactivated() {
        // given
        Room room = mock(Room.class);

        UserRoom userRoom = mock(UserRoom.class);

        when(roomStore.getRoomEnteredByUser(USER_ID, ROOM_ID)).thenReturn(room);
        when(roomStore.getUserRoomByDeleteStatus(USER_ID, room, DeleteStatus.NOT_DELETED))
                .thenReturn(Optional.of(userRoom));
        when(roomStore.countUserRoom(room)).thenReturn(0);

        // when
        roomService.leaveRoom(ROOM_ID, USER_ID);

        // then
        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(roomStore).getUserRoomByDeleteStatus(USER_ID, room, DeleteStatus.NOT_DELETED);
        verify(userRoom).leave();
        verify(roomStore).countUserRoom(room);
        verify(room).deactivate();
    }

    @Test
    @DisplayName("방 조회 성공")
    void getAllRoomInfo_Success() {
        // given
        PageDto pageDto = PageDto.builder()
                .userId(USER_ID)
                .page(0)
                .size(10)
                .build();

        List<Room> roomList = List.of(mock(Room.class), mock(Room.class));

        when(roomStore.getAllRoomEnteredByUser(pageDto)).thenReturn(roomList);
        when(roomStore.getAllMemberInfo(any(Room.class))).thenReturn(new ArrayList<>());

        // when
        List<RoomDetail> result = roomService.getAllRoomInfo(pageDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        verify(roomStore).getAllRoomEnteredByUser(pageDto);
        verify(roomStore, times(2)).getAllMemberInfo(any(Room.class));
    }
}
