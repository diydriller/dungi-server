package com.dungi.apiserver.presentation.room.controller;

import com.dungi.apiserver.application.room.service.RoomService;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.core.domain.room.model.Room;
import com.dungi.core.domain.user.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.dungi.common.util.StringUtil.LOGIN_USER;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan(basePackages = {"com.dungi.apiserver.presentation.room.controller", "com.dungi.apiserver.web"})
@ActiveProfiles("test")
@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;
    private User testUser;
    private final Long ROOM_ID = 1L;
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        testUser = mock(User.class);
        when(testUser.getId()).thenReturn(USER_ID);
        when(testUser.getEmail()).thenReturn("test@example.com");
        when(testUser.getNickname()).thenReturn("testUser");
        session.setAttribute(LOGIN_USER, testUser);
    }

    @Test
    @DisplayName("방 입장 성공")
    void enterRoom_Success() throws Exception {
        // given
        Room room = mock(Room.class);
        when(room.getId()).thenReturn(ROOM_ID);
        when(room.getName()).thenReturn("테스트 방");
        when(room.getColor()).thenReturn("blue");

        when(roomService.enterRoom(ROOM_ID, USER_ID)).thenReturn(room);

        // when & then
        mockMvc.perform(post("/room/{roomId}/member", ROOM_ID)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.roomId").value(ROOM_ID))
                .andExpect(jsonPath("$.data.roomName").value("테스트 방"))
                .andExpect(jsonPath("$.data.roomColor").value("blue"))
                .andExpect(jsonPath("$.data.userId").value(USER_ID));
    }

    @Test
    @DisplayName("방 입장 실패 - 로그인하지 않은 사용자")
    void enterRoom_Fail_NotLoggedIn() throws Exception {
        // given
        MockHttpSession emptySession = new MockHttpSession();

        // when & then
        mockMvc.perform(post("/room/{roomId}/member", ROOM_ID)
                        .session(emptySession))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("방 퇴장 성공 - 방이 비활성화되지 않음")
    void leaveRoom_Success_RoomNotDeactivated() throws Exception {
        // given
        Room room = mock(Room.class);
        when(room.getId()).thenReturn(ROOM_ID);
        when(room.getName()).thenReturn("테스트 방");


        // when & then
        mockMvc.perform(delete("/room/{roomId}/member", ROOM_ID)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("방 퇴장 성공 - 방이 비활성화됨")
    void leaveRoom_Success_RoomDeactivated() throws Exception {
        // given
        Room room = mock(Room.class);
        when(room.getId()).thenReturn(ROOM_ID);
        when(room.getName()).thenReturn("테스트 방");


        // when & then
        mockMvc.perform(delete("/room/{roomId}/member", ROOM_ID)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("방 퇴장 실패 - 로그인하지 않은 사용자")
    void leaveRoom_Fail_NotLoggedIn() throws Exception {
        // given
        MockHttpSession emptySession = new MockHttpSession();

        // when & then
        mockMvc.perform(delete("/room/{roomId}/member", ROOM_ID)
                        .session(emptySession))
                .andExpect(status().isInternalServerError());
    }
}
