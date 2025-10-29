package com.dungi.apiserver.presentation.todo.controller;

import com.dungi.apiserver.application.todo.dto.CreateRepeatTodoDto;
import com.dungi.apiserver.application.todo.dto.CreateTodayTodoDto;
import com.dungi.apiserver.application.todo.service.TodoService;
import com.dungi.apiserver.presentation.todo.dto.CreateRepeatTodoRequestDto;
import com.dungi.apiserver.presentation.todo.dto.CreateTodayTodoRequestDto;
import com.dungi.common.dto.PageDto;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.core.domain.todo.model.RepeatTodo;
import com.dungi.core.domain.todo.model.TodayTodo;
import com.dungi.core.domain.todo.model.Todo;
import com.dungi.core.domain.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.dungi.common.util.StringUtil.LOGIN_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan(basePackages = {"com.dungi.apiserver.presentation.todo.controller", "com.dungi.apiserver.web"})
@ActiveProfiles("test")
@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;
    private final Long ROOM_ID = 1L;
    private final Long TODO_ID = 1L;
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        User testUser = mock(User.class);
        when(testUser.getId()).thenReturn(USER_ID);
        when(testUser.getEmail()).thenReturn("test@example.com");
        when(testUser.getNickname()).thenReturn("testUser");
        session.setAttribute(LOGIN_USER, testUser);
    }

    @Test
    @DisplayName("오늘 할일 생성 성공")
    void createTodayTodo_Success() throws Exception {
        // given
        CreateTodayTodoRequestDto requestDto = CreateTodayTodoRequestDto.builder()
                .todo("테스트 할일")
                .time("2025/07/10/14/30")
                .build();

        Todo createdTodo = mock(Todo.class);
        when(createdTodo.getId()).thenReturn(TODO_ID);
        when(createdTodo.getTodoItem()).thenReturn("테스트 할일");

        when(todoService.createTodayTodo(any(CreateTodayTodoDto.class), eq(USER_ID), eq(ROOM_ID)))
                .thenReturn(createdTodo);

        // when & then
        mockMvc.perform(post("/room/{roomId}/todo/day", ROOM_ID)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.id").value(TODO_ID))
                .andExpect(jsonPath("$.data.todoItem").value("테스트 할일"));
    }

    @Test
    @DisplayName("반복 할일 생성 성공")
    void createRepeatTodo_Success() throws Exception {
        // given
        CreateRepeatTodoRequestDto requestDto = CreateRepeatTodoRequestDto.builder()
                .todo("반복 할일")
                .time("09/00")
                .days("1111111")
                .build();

        RepeatTodo createdRepeatTodo = mock(RepeatTodo.class);
        when(createdRepeatTodo.getId()).thenReturn(TODO_ID);
        when(createdRepeatTodo.getTodoItem()).thenReturn("반복 할일");
        when(createdRepeatTodo.getDeadline()).thenReturn(LocalDateTime.now());
        when(createdRepeatTodo.getUserId()).thenReturn(USER_ID);

        when(todoService.createRepeatTodo(any(CreateRepeatTodoDto.class), eq(USER_ID), eq(ROOM_ID)))
                .thenReturn(createdRepeatTodo);

        // when & then
        mockMvc.perform(post("/room/{roomId}/todo/days", ROOM_ID)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.todoId").value(TODO_ID))
                .andExpect(jsonPath("$.data.todo").value("반복 할일"))
                .andExpect(jsonPath("$.data.userId").value(USER_ID));
    }

    @Test
    @DisplayName("오늘 할일 조회 성공")
    void getTodayTodo_Success() throws Exception {
        // given
        List<TodayTodo> todayTodoList = Arrays.asList(
                createMockTodayTodo(1L, "할일1"),
                createMockTodayTodo(2L, "할일2")
        );

        when(todoService.getTodayTodo(any(PageDto.class))).thenReturn(todayTodoList);

        // when & then
        mockMvc.perform(get("/room/{roomId}/todo/day", ROOM_ID)
                        .session(session)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("반복 할일 조회 성공")
    void getRepeatTodo_Success() throws Exception {
        // given
        List<com.dungi.apiserver.application.todo.dto.GetRepeatTodoDto> repeatTodoList = Arrays.asList(
                com.dungi.apiserver.application.todo.dto.GetRepeatTodoDto.builder()
                        .todoId(1L)
                        .todo("반복 할일1")
                        .deadline(LocalDateTime.of(2025, 7, 20, 17, 20))
                        .day("1111111")
                        .userId(1L)
                        .build(),
                com.dungi.apiserver.application.todo.dto.GetRepeatTodoDto.builder()
                        .todoId(2L)
                        .todo("반복 할일2")
                        .deadline(LocalDateTime.of(2025, 7, 20, 10, 10))
                        .day("1101110")
                        .userId(3L)
                        .build()
        );

        when(todoService.getRepeatTodo(any(PageDto.class))).thenReturn(repeatTodoList);

        // when & then
        mockMvc.perform(get("/room/{roomId}/todo/days", ROOM_ID)
                        .session(session)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("오늘 할일 완료 성공")
    void completeTodayTodo_Success() throws Exception {
        // given
        TodayTodo completedTodo = mock(TodayTodo.class);
        when(completedTodo.getId()).thenReturn(TODO_ID);
        when(completedTodo.getTodoItem()).thenReturn("완료된 할일");
        when(completedTodo.getUserId()).thenReturn(USER_ID);
        when(completedTodo.getRoomId()).thenReturn(ROOM_ID);
        when(completedTodo.getDeadline()).thenReturn(LocalDateTime.now());

        when(todoService.completeTodayTodo(USER_ID, ROOM_ID, TODO_ID)).thenReturn(completedTodo);

        // when & then
        mockMvc.perform(patch("/room/{roomId}/todo/{todoId}/day", ROOM_ID, TODO_ID)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.todoId").value(TODO_ID))
                .andExpect(jsonPath("$.data.todo").value("완료된 할일"))
                .andExpect(jsonPath("$.data.userId").value(USER_ID))
                .andExpect(jsonPath("$.data.roomId").value(ROOM_ID));
    }

    @Test
    @DisplayName("칭찬하기 성공")
    void complimentMember_Success() throws Exception {
        // given
        Long memberId = 2L;

        // when & then
        mockMvc.perform(post("/room/{roomId}/compliment", ROOM_ID)
                        .session(session)
                        .param("memberId", memberId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.senderId").value(USER_ID))
                .andExpect(jsonPath("$.data.receiverId").value(memberId));
    }

    @Test
    @DisplayName("오늘 할일 생성 실패 - 유효하지 않은 요청")
    void createTodayTodo_Fail_InvalidRequest() throws Exception {
        // given
        CreateTodayTodoRequestDto requestDto = CreateTodayTodoRequestDto.builder()
                .todo("")
                .time("invalid-time")
                .build();

        // when & then
        mockMvc.perform(post("/room/{roomId}/todo/day", ROOM_ID)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("오늘 할일 생성 실패 - 로그인하지 않은 사용자")
    void createTodayTodo_Fail_NotLoggedIn() throws Exception {
        // given
        CreateTodayTodoRequestDto requestDto = CreateTodayTodoRequestDto.builder()
                .todo("테스트 할일")
                .time("2025/07/10/14/30")
                .build();

        MockHttpSession emptySession = new MockHttpSession();

        // when & then
        mockMvc.perform(post("/room/{roomId}/todo/day", ROOM_ID)
                        .session(emptySession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError());
    }

    private TodayTodo createMockTodayTodo(Long id, String todoItem) {
        TodayTodo todayTodo = mock(TodayTodo.class);
        when(todayTodo.getId()).thenReturn(id);
        when(todayTodo.getTodoItem()).thenReturn(todoItem);
        when(todayTodo.getUserId()).thenReturn(USER_ID);
        when(todayTodo.getDeadline()).thenReturn(LocalDateTime.now());
        return todayTodo;
    }
}
