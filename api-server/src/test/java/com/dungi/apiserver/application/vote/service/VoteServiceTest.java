package com.dungi.apiserver.application.vote.service;

import com.dungi.apiserver.application.vote.dto.CreateVoteDto;
import com.dungi.core.domain.room.model.Room;
import com.dungi.core.domain.summary.event.SaveNoticeVoteEvent;
import com.dungi.core.domain.vote.model.UserVoteItem;
import com.dungi.core.domain.vote.model.Vote;
import com.dungi.core.domain.vote.model.VoteItem;
import com.dungi.core.integration.message.common.MessagePublisher;
import com.dungi.core.integration.store.room.RoomStore;
import com.dungi.core.integration.store.vote.VoteStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.dungi.common.util.StringUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

    @Mock
    private VoteStore voteStore;

    @Mock
    private RoomStore roomStore;

    @Mock
    private MessagePublisher messagePublisher;

    @InjectMocks
    private VoteService voteService;

    private final Long ROOM_ID = 1L;
    private final Long USER_ID = 1L;
    private final Long VOTE_ID = 1L;
    private final Long CHOICE_ID = 1L;

    @BeforeEach
    void setUp() {
        when(roomStore.getRoomEnteredByUser(anyLong(), anyLong())).thenReturn(mock(Room.class));
    }

    @Test
    @DisplayName("투표 생성 성공")
    void createVote_Success() {
        // given
        CreateVoteDto dto = CreateVoteDto.builder()
                .title("테스트 투표")
                .choiceList(Arrays.asList("선택지1", "선택지2", "선택지3"))
                .build();

        Vote savedVote = mock(Vote.class);
        when(savedVote.getId()).thenReturn(VOTE_ID);
        when(savedVote.getTitle()).thenReturn("테스트 투표");
        when(savedVote.getRoomId()).thenReturn(ROOM_ID);
        when(savedVote.getUserId()).thenReturn(USER_ID);
        when(savedVote.getCreatedTime()).thenReturn(LocalDateTime.now());

        when(voteStore.saveVote(any(Vote.class), anyList())).thenReturn(savedVote);

        // when
        Vote result = voteService.createVote(dto, USER_ID, ROOM_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(VOTE_ID);
        assertThat(result.getTitle()).isEqualTo("테스트 투표");
        assertThat(result.getRoomId()).isEqualTo(ROOM_ID);
        assertThat(result.getUserId()).isEqualTo(USER_ID);

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(voteStore).saveVote(any(Vote.class), anyList());
        verify(messagePublisher).publish(any(SaveNoticeVoteEvent.class), eq(SAVE_NOTICE_VOTE_TOPIC));
    }

    @Test
    @DisplayName("투표 생성 시 메시지 발행 검증")
    void createVote_MessagePublishing() {
        // given
        CreateVoteDto dto = CreateVoteDto.builder()
                .title("테스트 투표")
                .choiceList(Arrays.asList("선택지1", "선택지2"))
                .build();

        Vote savedVote = mock(Vote.class);
        when(savedVote.getId()).thenReturn(VOTE_ID);
        when(savedVote.getCreatedTime()).thenReturn(LocalDateTime.now());

        when(voteStore.saveVote(any(Vote.class), anyList())).thenReturn(savedVote);

        // when
        voteService.createVote(dto, USER_ID, ROOM_ID);

        // then
        verify(messagePublisher).publish(argThat(event -> {
            SaveNoticeVoteEvent saveEvent = (SaveNoticeVoteEvent) event;
            return saveEvent.getId().equals(VOTE_ID) &&
                   saveEvent.getType().equals(VOTE_TYPE) &&
                   saveEvent.getContent().equals("테스트 투표") &&
                   saveEvent.getRoomId().equals(ROOM_ID) &&
                   saveEvent.getUserId().equals(USER_ID);
        }), eq(SAVE_NOTICE_VOTE_TOPIC));
    }

    @Test
    @DisplayName("투표 생성 시 선택지 개수 검증")
    void createVote_ChoiceListSize() {
        // given
        List<String> choiceList = Arrays.asList("선택지1", "선택지2", "선택지3");
        CreateVoteDto dto = CreateVoteDto.builder()
                .title("테스트 투표")
                .choiceList(choiceList)
                .build();

        Vote savedVote = mock(Vote.class);
        when(savedVote.getId()).thenReturn(VOTE_ID);

        when(voteStore.saveVote(any(Vote.class), anyList())).thenReturn(savedVote);

        // when
        voteService.createVote(dto, USER_ID, ROOM_ID);

        // then
        verify(voteStore).saveVote(any(Vote.class), argThat(voteItems -> {
            return voteItems.size() == 3 &&
                   voteItems.stream().anyMatch(item -> item.getChoice().equals("선택지1")) &&
                   voteItems.stream().anyMatch(item -> item.getChoice().equals("선택지2")) &&
                   voteItems.stream().anyMatch(item -> item.getChoice().equals("선택지3"));
        }));
    }

    @Test
    @DisplayName("투표 선택 성공 - 새로운 선택")
    void createVoteChoice_Success_NewChoice() {
        // given
        VoteItem voteItem = mock(VoteItem.class);
        when(voteItem.getId()).thenReturn(CHOICE_ID);
        when(voteItem.getChoice()).thenReturn("선택지1");

        when(voteStore.getVoteItem(CHOICE_ID, VOTE_ID)).thenReturn(voteItem);
        when(voteStore.getVoteChoice(USER_ID, voteItem)).thenReturn(Optional.empty());

        // when
        UserVoteItem result = voteService.createVoteChoice(ROOM_ID, USER_ID, VOTE_ID, CHOICE_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNull();
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getVoteItem().getId()).isEqualTo(CHOICE_ID);
        assertThat(result.getVoteItem().getChoice()).isEqualTo("선택지1");

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(voteStore).getVoteItem(CHOICE_ID, VOTE_ID);
        verify(voteStore).getVoteChoice(USER_ID, voteItem);
        verify(voteStore).saveUserVoteChoice(any(UserVoteItem.class));
    }

    @Test
    @DisplayName("투표 선택 성공 - 기존 선택 변경")
    void createVoteChoice_Success_ChangeChoice() {
        // given
        VoteItem voteItem = mock(VoteItem.class);
        when(voteItem.getId()).thenReturn(CHOICE_ID);
        when(voteItem.getChoice()).thenReturn("선택지2");

        UserVoteItem existingUserVoteItem = mock(UserVoteItem.class);
        when(existingUserVoteItem.getId()).thenReturn(1L);
        when(existingUserVoteItem.getUserId()).thenReturn(USER_ID);
        when(existingUserVoteItem.getVoteItem()).thenReturn(voteItem);

        when(voteStore.getVoteItem(CHOICE_ID, VOTE_ID)).thenReturn(voteItem);
        when(voteStore.getVoteChoice(USER_ID, voteItem)).thenReturn(Optional.of(existingUserVoteItem));

        // when
        UserVoteItem result = voteService.createVoteChoice(ROOM_ID, USER_ID, VOTE_ID, CHOICE_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        assertThat(result.getVoteItem().getId()).isEqualTo(CHOICE_ID);
        assertThat(result.getVoteItem().getChoice()).isEqualTo("선택지2");

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(voteStore).getVoteItem(CHOICE_ID, VOTE_ID);
        verify(voteStore).getVoteChoice(USER_ID, voteItem);
        verify(existingUserVoteItem).changeChoice();
        verify(voteStore).saveUserVoteChoice(existingUserVoteItem);
    }
}
