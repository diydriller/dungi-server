package com.dungi.apiserver.presentation.memo.controller;

import com.dungi.apiserver.application.memo.dto.CreateMemoDto;
import com.dungi.apiserver.application.memo.dto.UpdateMemoDto;
import com.dungi.apiserver.application.memo.service.MemoService;
import com.dungi.apiserver.presentation.memo.dto.CreateMemoRequestDto;
import com.dungi.apiserver.presentation.memo.dto.UpdateMemoRequestDto;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.core.domain.common.query.UserDetail;
import com.dungi.core.domain.memo.model.Memo;
import com.dungi.core.domain.memo.query.MemoDetail;
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

@ComponentScan(basePackages = {"com.dungi.apiserver.presentation.memo.controller", "com.dungi.apiserver.web"})
@ActiveProfiles("test")
@WebMvcTest(MemoController.class)
class MemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemoService memoService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;
    private User testUser;
    private final Long ROOM_ID = 1L;
    private final Long MEMO_ID = 1L;
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
    @DisplayName("메모 생성 성공")
    void createMemo_Success() throws Exception {
        // given
        CreateMemoRequestDto requestDto = CreateMemoRequestDto.builder()
                .memo("테스트 메모")
                .x(10.0)
                .y(20.0)
                .memoColor("red")
                .build();

        Memo createdMemo = mock(Memo.class);
        when(createdMemo.getId()).thenReturn(MEMO_ID);
        when(createdMemo.getUserId()).thenReturn(USER_ID);
        when(createdMemo.getRoomId()).thenReturn(ROOM_ID);
        when(createdMemo.getMemoItem()).thenReturn(requestDto.getMemo());
        when(createdMemo.getXPosition()).thenReturn(requestDto.getX());
        when(createdMemo.getYPosition()).thenReturn(requestDto.getY());
        when(createdMemo.getMemoColor()).thenReturn(requestDto.getMemoColor());

        when(memoService.createMemo(any(CreateMemoDto.class), eq(ROOM_ID), eq(USER_ID)))
                .thenReturn(createdMemo);

        // when & then
        mockMvc.perform(post("/room/{roomId}/memo", ROOM_ID)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.memoId").value(MEMO_ID))
                .andExpect(jsonPath("$.data.memoItem").value("테스트 메모"))
                .andExpect(jsonPath("$.data.x").value(10.0))
                .andExpect(jsonPath("$.data.y").value(20.0))
                .andExpect(jsonPath("$.data.memoColor").value("red"))
                .andExpect(jsonPath("$.data.roomId").value(ROOM_ID))
                .andExpect(jsonPath("$.data.userId").value(testUser.getId()));
    }

    @Test
    @DisplayName("메모 수정 성공")
    void updateMemo_Success() throws Exception {
        // given
        UpdateMemoRequestDto requestDto = UpdateMemoRequestDto.builder()
                .memo("수정된 메모")
                .memoColor("blue")
                .build();

        Memo updatedMemo = mock(Memo.class);
        when(updatedMemo.getId()).thenReturn(MEMO_ID);
        when(updatedMemo.getUserId()).thenReturn(USER_ID);
        when(updatedMemo.getRoomId()).thenReturn(ROOM_ID);
        when(updatedMemo.getMemoItem()).thenReturn(requestDto.getMemo());
        when(updatedMemo.getXPosition()).thenReturn(100.0);
        when(updatedMemo.getYPosition()).thenReturn(200.0);
        when(updatedMemo.getMemoColor()).thenReturn(requestDto.getMemoColor());

        when(memoService.updateMemo(any(UpdateMemoDto.class), eq(ROOM_ID), eq(USER_ID), eq(MEMO_ID)))
                .thenReturn(updatedMemo);

        // when & then
        mockMvc.perform(put("/room/{roomId}/memo/{memoId}/update", ROOM_ID, MEMO_ID)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.memoId").value(MEMO_ID))
                .andExpect(jsonPath("$.data.memoItem").value("수정된 메모"))
                .andExpect(jsonPath("$.data.memoColor").value("blue"))
                .andExpect(jsonPath("$.data.roomId").value(ROOM_ID))
                .andExpect(jsonPath("$.data.userId").value(testUser.getId()));
    }

    @Test
    @DisplayName("메모 조회 성공")
    void getMemo_Success() throws Exception {
        // given
        List<MemoDetail> memoDetails = Arrays.asList(
                MemoDetail.builder()
                        .id(1L)
                        .memoItem("메모1")
                        .xPosition(10.0)
                        .yPosition(20.0)
                        .memoColor("red")
                        .memoUser(
                                UserDetail.builder()
                                        .profileImg("http://localhost:8080/images/kim.png")
                                        .nickname("kim")
                                        .userId(1L)
                                        .build()
                        )
                        .createdTime(LocalDateTime.now().minusHours(1))
                        .build(),
                MemoDetail.builder()
                        .id(2L)
                        .memoItem("메모2")
                        .xPosition(30.0)
                        .yPosition(40.0)
                        .memoColor("blue")
                        .memoUser(
                                UserDetail.builder()
                                        .profileImg("http://localhost:8080/images/park.png")
                                        .nickname("park")
                                        .userId(2L)
                                        .build()
                        )
                        .createdTime(LocalDateTime.now().minusHours(2))
                        .build()
        );

        when(memoService.getMemo(ROOM_ID, testUser.getId())).thenReturn(memoDetails);

        // when & then
        mockMvc.perform(get("/room/{roomId}/memo", ROOM_ID)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].memo").value("메모1"))
                .andExpect(jsonPath("$.data[1].memo").value("메모2"));
    }

    @Test
    @DisplayName("메모 삭제 성공")
    void deleteMemo_Success() throws Exception {
        // when & then
        mockMvc.perform(delete("/room/{roomId}/memo/{memoId}", ROOM_ID, MEMO_ID)
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value(BaseResponseStatus.SUCCESS.getCode()))
                .andExpect(jsonPath("$.message").value(BaseResponseStatus.SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("메모 생성 실패 - 유효하지 않은 요청")
    void createMemo_Fail_InvalidRequest() throws Exception {
        // given
        CreateMemoRequestDto requestDto = CreateMemoRequestDto.builder()
                .memo("")
                .x(-1.0)
                .y(-1.0)
                .memoColor("")
                .build();

        // when & then
        mockMvc.perform(post("/room/{roomId}/memo", ROOM_ID)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메모 수정 실패 - 유효하지 않은 요청")
    void updateMemo_Fail_InvalidRequest() throws Exception {
        // given
        UpdateMemoRequestDto requestDto = UpdateMemoRequestDto.builder()
                .memo("") // 빈 문자열
                .memoColor("") // 빈 문자열
                .build();

        // when & then
        mockMvc.perform(put("/room/{roomId}/memo/{memoId}/update", ROOM_ID, MEMO_ID)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메모 생성 실패 - 로그인하지 않은 사용자")
    void createMemo_Fail_NotLoggedIn() throws Exception {
        // given
        CreateMemoRequestDto requestDto = CreateMemoRequestDto.builder()
                .memo("테스트 메모")
                .x(10.0)
                .y(20.0)
                .memoColor("red")
                .build();

        MockHttpSession emptySession = new MockHttpSession();

        // when & then
        mockMvc.perform(post("/room/{roomId}/memo", ROOM_ID)
                        .session(emptySession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError()); // NullPointerException 발생
    }
}
