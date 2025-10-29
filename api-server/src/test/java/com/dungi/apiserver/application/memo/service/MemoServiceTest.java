package com.dungi.apiserver.application.memo.service;

import com.dungi.apiserver.application.memo.dto.CreateMemoDto;
import com.dungi.apiserver.application.memo.dto.MoveMemoDto;
import com.dungi.apiserver.application.memo.dto.UpdateMemoDto;
import com.dungi.common.event.MemoCreateEvent;
import com.dungi.common.event.MemoDeleteEvent;
import com.dungi.common.event.MemoEditEvent;
import com.dungi.common.exception.BaseException;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.core.domain.memo.model.Memo;
import com.dungi.core.domain.memo.query.MemoDetail;
import com.dungi.core.domain.room.model.Room;
import com.dungi.core.integration.store.memo.MemoStore;
import com.dungi.core.integration.store.room.RoomStore;
import com.dungi.message.redis.publisher.RedisPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static com.dungi.common.util.StringUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemoServiceTest {

    @Mock
    private MemoStore memoStore;

    @Mock
    private RoomStore roomStore;

    @Mock
    private RedisPublisher redisPublisher;

    @InjectMocks
    private MemoService memoService;

    private final Long ROOM_ID = 1L;
    private final Long USER_ID = 1L;
    private final Long MEMO_ID = 1L;

    @BeforeEach
    void setUp() {
        when(roomStore.getRoomEnteredByUser(anyLong(), anyLong())).thenReturn(mock(Room.class));
    }

    @Test
    @DisplayName("메모 생성 성공")
    void createMemo_Success() {
        // given
        CreateMemoDto dto = CreateMemoDto.builder()
                .memoItem("테스트 메모")
                .xPosition(10.0)
                .yPosition(20.0)
                .memoColor("red")
                .build();

        Memo savedMemo = mock(Memo.class);
        when(savedMemo.getId()).thenReturn(MEMO_ID);
        when(savedMemo.getMemoItem()).thenReturn(dto.getMemoItem());
        when(savedMemo.getXPosition()).thenReturn(dto.getXPosition());
        when(savedMemo.getYPosition()).thenReturn(dto.getYPosition());
        when(savedMemo.getMemoColor()).thenReturn(dto.getMemoColor());

        when(memoStore.saveMemo(any(Memo.class))).thenReturn(savedMemo);

        // when
        Memo result = memoService.createMemo(dto, ROOM_ID, USER_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(MEMO_ID);
        assertThat(result.getMemoItem()).isEqualTo("테스트 메모");
        assertThat(result.getXPosition()).isEqualTo(10.0);
        assertThat(result.getYPosition()).isEqualTo(20.0);
        assertThat(result.getMemoColor()).isEqualTo("red");

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(memoStore).saveMemo(any(Memo.class));
        verify(redisPublisher).publish(eq(MEMO_CREATE_CHANNEL), any(MemoCreateEvent.class));
    }

    @Test
    @DisplayName("메모 수정 성공")
    void updateMemo_Success() {
        // given
        UpdateMemoDto dto = UpdateMemoDto.builder()
                .memo("수정된 메모")
                .memoColor("blue")
                .build();

        Memo existingMemo = mock(Memo.class);
        when(existingMemo.getId()).thenReturn(MEMO_ID);
        when(existingMemo.getUserId()).thenReturn(USER_ID);
        when(existingMemo.getXPosition()).thenReturn(10.0);
        when(existingMemo.getYPosition()).thenReturn(20.0);

        Memo updatedMemo = mock(Memo.class);
        when(updatedMemo.getId()).thenReturn(MEMO_ID);
        when(updatedMemo.getMemoItem()).thenReturn("수정된 메모");
        when(updatedMemo.getMemoColor()).thenReturn("blue");

        when(memoStore.getMemo(MEMO_ID)).thenReturn(existingMemo);
        when(existingMemo.updateMemo(dto.getMemo(), dto.getMemoColor())).thenReturn(updatedMemo);

        // when
        Memo result = memoService.updateMemo(dto, ROOM_ID, USER_ID, MEMO_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(MEMO_ID);
        assertThat(result.getMemoItem()).isEqualTo("수정된 메모");
        assertThat(result.getMemoColor()).isEqualTo("blue");

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(memoStore).getMemo(MEMO_ID);
        verify(redisPublisher).publish(eq(MEMO_EDIT_CHANNEL), any(MemoEditEvent.class));
    }

    @Test
    @DisplayName("메모 수정 실패 - 권한 없음")
    void updateMemo_Fail_Unauthorized() {
        // given
        UpdateMemoDto dto = UpdateMemoDto.builder()
                .memo("수정된 메모")
                .memoColor("blue")
                .build();

        Memo existingMemo = Memo.builder()
                .id(MEMO_ID)
                .userId(999L)
                .roomId(ROOM_ID)
                .memoItem("기존 메모")
                .xPosition(10.0)
                .yPosition(20.0)
                .memoColor("red")
                .build();

        when(memoStore.getMemo(MEMO_ID)).thenReturn(existingMemo);

        // when & then
        assertThatThrownBy(() -> memoService.updateMemo(dto, ROOM_ID, USER_ID, MEMO_ID))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("status", BaseResponseStatus.AUTHORIZATION_ERROR);

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(memoStore).getMemo(MEMO_ID);
        verify(redisPublisher, never()).publish(anyString(), any());
    }

    @Test
    @DisplayName("메모 이동 성공")
    void moveMemo_Success() {
        // given
        MoveMemoDto dto = MoveMemoDto.builder()
                .x(30.0)
                .y(40.0)
                .build();

        Memo existingMemo = Memo.builder()
                .id(MEMO_ID)
                .userId(USER_ID)
                .roomId(ROOM_ID)
                .memoItem("테스트 메모")
                .xPosition(10.0)
                .yPosition(20.0)
                .memoColor("red")
                .build();

        when(memoStore.getMemo(MEMO_ID)).thenReturn(existingMemo);

        // when
        Memo result = memoService.moveMemo(dto, ROOM_ID, USER_ID, MEMO_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(MEMO_ID);
        assertThat(result.getXPosition()).isEqualTo(30.0);
        assertThat(result.getYPosition()).isEqualTo(40.0);

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(memoStore).getMemo(MEMO_ID);
        verify(redisPublisher).publish(eq(MEMO_EDIT_CHANNEL), any(MemoEditEvent.class));
    }

    @Test
    @DisplayName("메모 삭제 성공")
    void deleteMemo_Success() {
        // given
        Memo existingMemo = Memo.builder()
                .id(MEMO_ID)
                .userId(USER_ID)
                .roomId(ROOM_ID)
                .memoItem("테스트 메모")
                .xPosition(10.0)
                .yPosition(20.0)
                .memoColor("red")
                .build();

        when(memoStore.getMemo(MEMO_ID)).thenReturn(existingMemo);

        // when
        memoService.deleteMemo(ROOM_ID, USER_ID, MEMO_ID);

        // then
        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(memoStore).getMemo(MEMO_ID);
        verify(redisPublisher).publish(eq(MEMO_DELETE_CHANNEL), any(MemoDeleteEvent.class));
    }

    @Test
    @DisplayName("메모 삭제 실패 - 권한 없음")
    void deleteMemo_Fail_Unauthorized() {
        // given
        Memo existingMemo = Memo.builder()
                .id(MEMO_ID)
                .userId(999L)
                .roomId(ROOM_ID)
                .memoItem("테스트 메모")
                .xPosition(10.0)
                .yPosition(20.0)
                .memoColor("red")
                .build();

        when(memoStore.getMemo(MEMO_ID)).thenReturn(existingMemo);

        // when & then
        assertThatThrownBy(() -> memoService.deleteMemo(ROOM_ID, USER_ID, MEMO_ID))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("status", BaseResponseStatus.AUTHORIZATION_ERROR);

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(memoStore).getMemo(MEMO_ID);
        verify(redisPublisher, never()).publish(anyString(), any());
    }

    @Test
    @DisplayName("메모 조회 성공")
    void getMemo_Success() {
        // given
        List<MemoDetail> memoDetails = Arrays.asList(
                MemoDetail.builder()
                        .id(1L)
                        .memoItem("메모1")
                        .xPosition(10.0)
                        .yPosition(20.0)
                        .memoColor("red")
                        .build(),
                MemoDetail.builder()
                        .id(2L)
                        .memoItem("메모2")
                        .xPosition(30.0)
                        .yPosition(40.0)
                        .memoColor("blue")
                        .build()
        );

        when(memoStore.getAllMemo(USER_ID, ROOM_ID)).thenReturn(memoDetails);

        // when
        List<MemoDetail> result = memoService.getMemo(ROOM_ID, USER_ID);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMemoItem()).isEqualTo("메모1");
        assertThat(result.get(1).getMemoItem()).isEqualTo("메모2");

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(memoStore).getAllMemo(USER_ID, ROOM_ID);
    }
}
