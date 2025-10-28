package com.dungi.apiserver.presentation.vote.controller;

import com.dungi.apiserver.application.vote.dto.CreateVoteDto;
import com.dungi.apiserver.application.vote.service.VoteService;
import com.dungi.apiserver.presentation.vote.dto.CreateVoteRequestDto;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.core.domain.user.model.User;
import com.dungi.core.domain.vote.model.UserVoteItem;
import com.dungi.core.domain.vote.model.Vote;
import com.dungi.core.domain.vote.model.VoteItem;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ComponentScan(basePackages = {"com.dungi.apiserver.presentation.vote.controller", "com.dungi.apiserver.web"})
@ActiveProfiles("test")
@WebMvcTest(VoteController.class)
class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoteService voteService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;
    private User testUser;
    private final Long ROOM_ID = 1L;
    private final Long VOTE_ID = 1L;
    private final Long CHOICE_ID = 1L;
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
    @DisplayName("투표 생성 성공")
    void createVote_Success() throws Exception {
        // given
        CreateVoteRequestDto requestDto = CreateVoteRequestDto.builder()
                .title("테스트 투표")
                .choiceList(Arrays.asList("선택지1", "선택지2", "선택지3"))
                .build();

        Vote createdVote = mock(Vote.class);
        when(createdVote.getId()).thenReturn(VOTE_ID);
        when(createdVote.getTitle()).thenReturn("테스트 투표");
        when(createdVote.getRoomId()).thenReturn(ROOM_ID);
        when(createdVote.getUserId()).thenReturn(USER_ID);
        when(createdVote.getCreatedTime()).thenReturn(LocalDateTime.now());

        when(voteService.createVote(any(CreateVoteDto.class), eq(USER_ID), eq(ROOM_ID)))
                .thenReturn(createdVote);

        // when & then
        mockMvc.perform(post("/room/{roomId}/vote", ROOM_ID)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.voteId").value(VOTE_ID))
                .andExpect(jsonPath("$.data.title").value("테스트 투표"))
                .andExpect(jsonPath("$.data.roomId").value(ROOM_ID))
                .andExpect(jsonPath("$.data.userId").value(USER_ID));
    }

    @Test
    @DisplayName("투표 생성 실패 - 유효하지 않은 요청")
    void createVote_Fail_InvalidRequest() throws Exception {
        // given
        CreateVoteRequestDto requestDto = CreateVoteRequestDto.builder()
                .title("")
                .choiceList(List.of())
                .build();

        // when & then
        mockMvc.perform(post("/room/{roomId}/vote", ROOM_ID)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("투표 생성 실패 - 로그인하지 않은 사용자")
    void createVote_Fail_NotLoggedIn() throws Exception {
        // given
        CreateVoteRequestDto requestDto = CreateVoteRequestDto.builder()
                .title("테스트 투표")
                .choiceList(Arrays.asList("선택지1", "선택지2"))
                .build();

        MockHttpSession emptySession = new MockHttpSession();

        // when & then
        mockMvc.perform(post("/room/{roomId}/vote", ROOM_ID)
                        .session(emptySession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("투표 선택 성공")
    void createVoteChoice_Success() throws Exception {
        // given
        UserVoteItem userVoteItem = mock(UserVoteItem.class);
        VoteItem voteItem = mock(VoteItem.class);
        when(voteItem.getId()).thenReturn(CHOICE_ID);
        when(voteItem.getChoice()).thenReturn("선택지1");

        when(userVoteItem.getId()).thenReturn(1L);
        when(userVoteItem.getUserId()).thenReturn(USER_ID);
        when(userVoteItem.getVoteItem()).thenReturn(voteItem);
        when(userVoteItem.getCreatedTime()).thenReturn(LocalDateTime.now());

        when(voteService.createVoteChoice(ROOM_ID, USER_ID, VOTE_ID, CHOICE_ID))
                .thenReturn(userVoteItem);

        // when & then
        mockMvc.perform(patch("/room/{roomId}/vote/{voteId}/choice/{choiceId}", ROOM_ID, VOTE_ID, CHOICE_ID)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.userVoteItemId").value(1L))
                .andExpect(jsonPath("$.data.userId").value(USER_ID))
                .andExpect(jsonPath("$.data.voteItemId").value(CHOICE_ID))
                .andExpect(jsonPath("$.data.choice").value("선택지1"));
    }

    @Test
    @DisplayName("투표 선택 실패 - 로그인하지 않은 사용자")
    void createVoteChoice_Fail_NotLoggedIn() throws Exception {
        // given
        MockHttpSession emptySession = new MockHttpSession();

        // when & then
        mockMvc.perform(patch("/room/{roomId}/vote/{voteId}/choice/{choiceId}", ROOM_ID, VOTE_ID, CHOICE_ID)
                        .session(emptySession))
                .andExpect(status().isInternalServerError());
    }
}
