package com.dungi.apiserver.presentation.notice.controller;

import com.dungi.apiserver.application.notice.dto.CreateNoticeDto;
import com.dungi.apiserver.application.notice.service.NoticeService;
import com.dungi.apiserver.presentation.notice.dto.CreateNoticeRequestDto;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.core.domain.notice.model.Notice;
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

import static com.dungi.common.util.StringUtil.LOGIN_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan(basePackages = {"com.dungi.apiserver.presentation.notice.controller", "com.dungi.apiserver.web"})
@ActiveProfiles("test")
@WebMvcTest(NoticeController.class)
class NoticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoticeService noticeService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;
    private User testUser;
    private final Long ROOM_ID = 1L;
    private final Long NOTICE_ID = 1L;
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
    @DisplayName("공지 생성 성공")
    void createNotice_Success() throws Exception {
        // given
        CreateNoticeRequestDto requestDto = CreateNoticeRequestDto.builder()
                .notice("테스트 공지")
                .build();

        Notice createdNotice = mock(Notice.class);
        when(createdNotice.getId()).thenReturn(NOTICE_ID);
        when(createdNotice.getNoticeItem()).thenReturn("테스트 공지");
        when(createdNotice.getRoomId()).thenReturn(ROOM_ID);
        when(createdNotice.getUserId()).thenReturn(USER_ID);
        when(createdNotice.getCreatedTime()).thenReturn(LocalDateTime.now());

        when(noticeService.createNotice(any(CreateNoticeDto.class)))
                .thenReturn(createdNotice);

        // when & then
        mockMvc.perform(post("/room/{roomId}/notice", ROOM_ID)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.noticeId").value(NOTICE_ID))
                .andExpect(jsonPath("$.data.noticeItem").value("테스트 공지"))
                .andExpect(jsonPath("$.data.roomId").value(ROOM_ID))
                .andExpect(jsonPath("$.data.userId").value(testUser.getId()));
    }

    @Test
    @DisplayName("공지 생성 실패 - 유효하지 않은 요청")
    void createNotice_Fail_InvalidRequest() throws Exception {
        // given
        CreateNoticeRequestDto requestDto = CreateNoticeRequestDto.builder()
                .notice("")
                .build();

        // when & then
        mockMvc.perform(post("/room/{roomId}/notice", ROOM_ID)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("공지 생성 실패 - 로그인하지 않은 사용자")
    void createNotice_Fail_NotLoggedIn() throws Exception {
        // given
        CreateNoticeRequestDto requestDto = CreateNoticeRequestDto.builder()
                .notice("테스트 공지")
                .build();

        MockHttpSession emptySession = new MockHttpSession();

        // when & then
        mockMvc.perform(post("/room/{roomId}/notice", ROOM_ID)
                        .session(emptySession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError());
    }
}
