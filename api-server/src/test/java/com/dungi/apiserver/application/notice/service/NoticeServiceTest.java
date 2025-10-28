package com.dungi.apiserver.application.notice.service;

import com.dungi.apiserver.application.notice.dto.CreateNoticeDto;
import com.dungi.core.domain.notice.model.Notice;
import com.dungi.core.domain.room.model.Room;
import com.dungi.core.domain.summary.event.SaveNoticeVoteEvent;
import com.dungi.core.integration.message.common.MessagePublisher;
import com.dungi.core.integration.store.notice.NoticeStore;
import com.dungi.core.integration.store.room.RoomStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.dungi.common.util.StringUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private RoomStore roomStore;

    @Mock
    private NoticeStore noticeStore;

    @Mock
    private MessagePublisher messagePublisher;

    @InjectMocks
    private NoticeService noticeService;

    private final Long ROOM_ID = 1L;
    private final Long USER_ID = 1L;
    private final Long NOTICE_ID = 1L;

    @BeforeEach
    void setUp() {
        when(roomStore.getRoomEnteredByUser(anyLong(), anyLong())).thenReturn(mock(Room.class));
    }

    @Test
    @DisplayName("공지 생성 성공")
    void createNotice_Success() {
        // given
        CreateNoticeDto dto = CreateNoticeDto.builder()
                .noticeItem("테스트 공지")
                .roomId(ROOM_ID)
                .userId(USER_ID)
                .build();

        Notice savedNotice = mock(Notice.class);
        when(savedNotice.getId()).thenReturn(NOTICE_ID);
        when(savedNotice.getNoticeItem()).thenReturn(dto.getNoticeItem());
        when(savedNotice.getRoomId()).thenReturn(ROOM_ID);
        when(savedNotice.getUserId()).thenReturn(USER_ID);
        when(savedNotice.getCreatedTime()).thenReturn(LocalDateTime.now());

        when(noticeStore.saveNotice(any(Notice.class))).thenReturn(savedNotice);

        // when
        Notice result = noticeService.createNotice(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(NOTICE_ID);
        assertThat(result.getNoticeItem()).isEqualTo("테스트 공지");
        assertThat(result.getRoomId()).isEqualTo(ROOM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(noticeStore).saveNotice(any(Notice.class));
        verify(messagePublisher).publish(any(SaveNoticeVoteEvent.class), eq(SAVE_NOTICE_VOTE_TOPIC));
    }

    @Test
    @DisplayName("공지 생성 시 메시지 발행 검증")
    void createNotice_MessagePublishing() {
        // given
        CreateNoticeDto dto = CreateNoticeDto.builder()
                .noticeItem("테스트 공지")
                .roomId(ROOM_ID)
                .userId(USER_ID)
                .build();

        Notice savedNotice = mock(Notice.class);
        when(savedNotice.getId()).thenReturn(NOTICE_ID);
        when(savedNotice.getCreatedTime()).thenReturn(LocalDateTime.now());

        when(noticeStore.saveNotice(any(Notice.class))).thenReturn(savedNotice);

        // when
        noticeService.createNotice(dto);

        // then
        verify(messagePublisher).publish(argThat(event -> {
            SaveNoticeVoteEvent saveEvent = (SaveNoticeVoteEvent) event;
            return saveEvent.getId().equals(NOTICE_ID) &&
                   saveEvent.getType().equals(NOTICE_TYPE) &&
                   saveEvent.getContent().equals("테스트 공지") &&
                   saveEvent.getRoomId().equals(ROOM_ID) &&
                   saveEvent.getUserId().equals(USER_ID);
        }), eq(SAVE_NOTICE_VOTE_TOPIC));
    }
}
