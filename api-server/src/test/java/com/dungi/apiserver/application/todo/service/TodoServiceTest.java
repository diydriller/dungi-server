package com.dungi.apiserver.application.todo.service;

import com.dungi.apiserver.application.todo.dto.CreateRepeatTodoDto;
import com.dungi.apiserver.application.todo.dto.CreateTodayTodoDto;
import com.dungi.apiserver.application.todo.dto.GetRepeatTodoDto;
import com.dungi.common.dto.PageDto;
import com.dungi.core.domain.common.value.NotificationType;
import com.dungi.core.domain.notification.query.NotificationDetail;
import com.dungi.core.domain.room.model.Room;
import com.dungi.core.domain.summary.event.UpdateWeeklyTodoCountEvent;
import com.dungi.core.domain.todo.model.RepeatTodo;
import com.dungi.core.domain.todo.model.TodayTodo;
import com.dungi.core.domain.todo.model.Todo;
import com.dungi.core.domain.todo.query.TodoStatistic;
import com.dungi.core.domain.user.model.User;
import com.dungi.core.integration.message.common.MessagePublisher;
import com.dungi.core.integration.store.room.RoomStore;
import com.dungi.core.integration.store.todo.TodoStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.dungi.common.util.StringUtil.NOTIFICATION_TOPIC;
import static com.dungi.common.util.StringUtil.UPDATE_WEEKLY_TODO_TOPIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoStore todoStore;

    @Mock
    private RoomStore roomStore;

    @Mock
    private MessagePublisher messagePublisher;

    @InjectMocks
    private TodoService todoService;

    private final Long ROOM_ID = 1L;
    private final Long USER_ID = 1L;
    private final Long TODO_ID = 1L;

    @Test
    @DisplayName("오늘 할일 생성 성공")
    void createTodayTodo_Success() {
        // given
        CreateTodayTodoDto dto = CreateTodayTodoDto.builder()
                .todo("테스트 할일")
                .time("2025/07/20/10/30")
                .build();

        TodayTodo savedTodo = mock(TodayTodo.class);
        when(savedTodo.getId()).thenReturn(TODO_ID);
        when(savedTodo.getTodoItem()).thenReturn("테스트 할일");

        when(todoStore.saveTodayTodo(any(TodayTodo.class))).thenReturn(savedTodo);

        // when
        Todo result = todoService.createTodayTodo(dto, USER_ID, ROOM_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TODO_ID);
        assertThat(result.getTodoItem()).isEqualTo("테스트 할일");

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(todoStore).saveTodayTodo(any(TodayTodo.class));
    }

    @Test
    @DisplayName("반복 할일 생성 성공")
    void createRepeatTodo_Success() {
        // given
        CreateRepeatTodoDto dto = CreateRepeatTodoDto.builder()
                .todo("반복 할일")
                .time("10/30")
                .days("1111111")
                .build();

        RepeatTodo savedRepeatTodo = mock(RepeatTodo.class);
        when(savedRepeatTodo.getId()).thenReturn(TODO_ID);
        when(savedRepeatTodo.getTodoItem()).thenReturn("반복 할일");

        when(todoStore.saveRepeatTodo(any(RepeatTodo.class), anyList())).thenReturn(savedRepeatTodo);

        // when
        RepeatTodo result = todoService.createRepeatTodo(dto, USER_ID, ROOM_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TODO_ID);
        assertThat(result.getTodoItem()).isEqualTo("반복 할일");

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(todoStore).saveRepeatTodo(any(RepeatTodo.class), anyList());
    }

    @Test
    @DisplayName("오늘 할일 조회 성공")
    void getTodayTodo_Success() {
        // given
        PageDto pageDto = PageDto.builder()
                .userId(USER_ID)
                .roomId(ROOM_ID)
                .page(0)
                .size(10)
                .build();

        List<TodayTodo> todayTodoList = Arrays.asList(mock(TodayTodo.class), mock(TodayTodo.class));
        when(todoStore.getTodayTodo(pageDto)).thenReturn(todayTodoList);

        // when
        List<TodayTodo> result = todoService.getTodayTodo(pageDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(todoStore).getTodayTodo(pageDto);
    }

    @Test
    @DisplayName("반복 할일 조회 성공")
    void getRepeatTodo_Success() {
        // given
        PageDto pageDto = PageDto.builder()
                .userId(USER_ID)
                .roomId(ROOM_ID)
                .page(0)
                .size(10)
                .build();

        List<RepeatTodo> repeatTodoList = Arrays.asList(
                createMockRepeatTodo(TODO_ID, "반복 할일1"),
                createMockRepeatTodo(2L, "반복 할일2")
        );
        when(todoStore.getRepeatTodo(pageDto)).thenReturn(repeatTodoList);

        // when
        List<GetRepeatTodoDto> result = todoService.getRepeatTodo(pageDto);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTodo()).isEqualTo("반복 할일1");
        assertThat(result.get(1).getTodo()).isEqualTo("반복 할일2");

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(todoStore).getRepeatTodo(pageDto);
    }

    @Test
    @DisplayName("최고 멤버 선정 성공")
    void findBestMember_Success() {
        // given
        Room room = mock(Room.class);
        List<User> members = Arrays.asList(
                createMockUser(1L),
                createMockUser(2L),
                createMockUser(3L)
        );

        List<TodoStatistic> memberTodoCounts = Arrays.asList(
                createMockTodoStatistic(1L, 5),
                createMockTodoStatistic(2L, 8),
                createMockTodoStatistic(3L, 8)
        );

        when(roomStore.getRoom(ROOM_ID)).thenReturn(room);
        when(roomStore.getAllMemberInRoom(room)).thenReturn(members);
        when(todoStore.getAllMemberTodoCount(anyList(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(memberTodoCounts);

        // when
        List<Long> result = todoService.findBestMember(ROOM_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(2L, 3L);

        verify(roomStore).getRoom(ROOM_ID);
        verify(roomStore).getAllMemberInRoom(room);
        verify(todoStore).getAllMemberTodoCount(anyList(), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("오늘 할일 완료 성공")
    void completeTodayTodo_Success() {
        // given
        TodayTodo todo = mock(TodayTodo.class);
        when(todo.getId()).thenReturn(TODO_ID);
        when(todo.getTodoItem()).thenReturn("완료된 할일");

        when(todoStore.findTodayTodo(ROOM_ID, TODO_ID)).thenReturn(todo);

        // when
        TodayTodo result = todoService.completeTodayTodo(USER_ID, ROOM_ID, TODO_ID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(TODO_ID);
        assertThat(result.getTodoItem()).isEqualTo("완료된 할일");

        verify(roomStore).getRoomEnteredByUser(USER_ID, ROOM_ID);
        verify(todoStore).findTodayTodo(ROOM_ID, TODO_ID);
        verify(todo).complete();
        verify(messagePublisher).publish(any(UpdateWeeklyTodoCountEvent.class), eq(UPDATE_WEEKLY_TODO_TOPIC));
    }

    @Test
    @DisplayName("칭찬하기 성공")
    void complimentMember_Success() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;

        // when
        todoService.complimentMember(senderId, receiverId);

        // then
        verify(messagePublisher).publish(argThat(notification -> {
            NotificationDetail detail = (NotificationDetail) notification;
            return detail.getSenderId().equals(senderId) &&
                    detail.getReceiverId().equals(receiverId) &&
                    detail.getType().equals(NotificationType.COMPLIMENT);
        }), eq(NOTIFICATION_TOPIC));
    }

    private User createMockUser(Long userId) {
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        return user;
    }

    private TodoStatistic createMockTodoStatistic(Long userId, int count) {
        TodoStatistic todoStatistic = mock(TodoStatistic.class);
        when(todoStatistic.getTodoCount()).thenReturn((long) count);
        lenient().when(todoStatistic.getUserId()).thenReturn(userId);
        return todoStatistic;
    }

    private RepeatTodo createMockRepeatTodo(Long id, String todoItem) {
        RepeatTodo repeatTodo = mock(RepeatTodo.class);
        when(repeatTodo.getId()).thenReturn(id);
        when(repeatTodo.getTodoItem()).thenReturn(todoItem);
        when(repeatTodo.getDeadline()).thenReturn(LocalDateTime.now());
        when(repeatTodo.getUserId()).thenReturn(USER_ID);
        return repeatTodo;
    }
}
