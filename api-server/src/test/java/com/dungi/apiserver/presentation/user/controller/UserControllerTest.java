package com.dungi.apiserver.presentation.user.controller;

import com.dungi.apiserver.application.user.service.UserService;
import com.dungi.apiserver.presentation.user.dto.LoginRequestDto;
import com.dungi.apiserver.web.TokenProvider;
import com.dungi.common.value.SnsProvider;
import com.dungi.core.domain.user.model.User;
import com.dungi.core.integration.store.user.AuthStore;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ComponentScan(basePackages = {"com.dungi.apiserver.presentation.user.controller", "com.dungi.apiserver.web"})
@ActiveProfiles("test")
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private AuthStore authStore;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String EMAIL = "test@example.com";
    private final String PASSWORD = "password";
    private final String NAME = "홍길동";
    private final String PHONE_NUMBER = "01012345678";
    private final String NICKNAME = "테스트유저";
    private final String PROFILE_IMG = "https://example.com/profile.jpg";
    private final String ACCESS_TOKEN = "access_token_123";
    private final String REFRESH_TOKEN = "refresh_token_123";

    @BeforeEach
    void setUp() {
        when(tokenProvider.createAccessToken(anyString())).thenReturn(ACCESS_TOKEN);
        when(tokenProvider.createRefreshToken()).thenReturn(REFRESH_TOKEN);
        when(tokenProvider.getExpirationDuration(anyString())).thenReturn(3600L);
    }

    @Test
    @DisplayName("일반 회원가입 성공")
    void join_Success() throws Exception {
        // given
        User user = createMockUser();
        when(userService.createUser(any())).thenReturn(user);

        MockMultipartFile imgFile = new MockMultipartFile(
                "img", "test.jpg", "image/jpeg", "test image content".getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/user")
                        .file(imgFile)
                        .param("email", EMAIL)
                        .param("password", PASSWORD)
                        .param("name", NAME)
                        .param("phoneNumber", PHONE_NUMBER)
                        .param("nickname", NICKNAME)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.email").value(EMAIL))
                .andExpect(jsonPath("$.data.name").value(NAME))
                .andExpect(jsonPath("$.data.nickname").value(NICKNAME))
                .andExpect(jsonPath("$.data.snsProvider").value("KAKAO"));
    }

    @Test
    @DisplayName("SNS 회원가입 성공")
    void kakaoJoin_Success() throws Exception {
        // given
        User user = createMockUser();
        when(userService.createSnsUser(any())).thenReturn(user);

        MockMultipartFile profileImgFile = new MockMultipartFile(
                "profileImg", "test.jpg", "image/jpeg", "test image content".getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/kakao/user")
                        .file(profileImgFile)
                        .param("email", EMAIL)
                        .param("nickname", NICKNAME)
                        .param("snsImg", PROFILE_IMG)
                        .param("serviceType", "KAKAO")
                        .param("accessToken", ACCESS_TOKEN)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.email").value(EMAIL))
                .andExpect(jsonPath("$.data.nickname").value(NICKNAME))
                .andExpect(jsonPath("$.data.snsProvider").value("KAKAO"));
    }

    @Test
    @DisplayName("이메일 중복 확인 성공")
    void checkEmail_Success() throws Exception {
        // given
        String requestJson = "{\"email\":\"" + EMAIL + "\"}";

        // when & then
        mockMvc.perform(post("/check/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    @DisplayName("SMS 전송 성공")
    void sendSms_Success() throws Exception {
        // given
        String requestJson = "{\"phoneNumber\":\"" + PHONE_NUMBER + "\"}";

        // when & then
        mockMvc.perform(post("/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    @DisplayName("인증번호 확인 성공")
    void checkCode_Success() throws Exception {
        // given
        String requestJson = "{\"code\":\"1234\",\"phoneNumber\":\"" + PHONE_NUMBER + "\"}";

        // when & then
        mockMvc.perform(post("/check/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() throws Exception {
        // given
        User user = createMockUser();
        when(userService.login(anyString(), anyString())).thenReturn(user);

        LoginRequestDto requestDto = LoginRequestDto.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .build();

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data.accessToken").value(ACCESS_TOKEN))
                .andExpect(jsonPath("$.data.refreshToken").value(REFRESH_TOKEN));
    }

    @Test
    @DisplayName("SNS 로그인 성공")
    void snsLogin_Success() throws Exception {
        // given
        User user = createMockUser();
        when(userService.snsLogin(any())).thenReturn(user);

        String requestJson = "{\"email\":\"" + EMAIL + "\",\"serviceType\":\"KAKAO\",\"accessToken\":\"" + ACCESS_TOKEN + "\"}";

        // when & then
        mockMvc.perform(post("/sns/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data.accessToken").value(ACCESS_TOKEN))
                .andExpect(jsonPath("$.data.refreshToken").value(REFRESH_TOKEN));
    }

    @Test
    @DisplayName("토큰 갱신 성공")
    void refresh_Success() throws Exception {
        // given
        when(authStore.validateRefreshTokenAndGetEmail(REFRESH_TOKEN)).thenReturn(EMAIL);

        String requestJson = "{\"refresh_token\":\"" + REFRESH_TOKEN + "\"}";

        // when & then
        mockMvc.perform(post("/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.data.accessToken").value(ACCESS_TOKEN))
                .andExpect(jsonPath("$.data.refreshToken").value(REFRESH_TOKEN));
    }

    private User createMockUser() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getName()).thenReturn(NAME);
        when(user.getPhoneNumber()).thenReturn(PHONE_NUMBER);
        when(user.getNickname()).thenReturn(NICKNAME);
        when(user.getProfileImg()).thenReturn(PROFILE_IMG);
        when(user.getSnsProvider()).thenReturn(SnsProvider.KAKAO);
        when(user.getCreatedTime()).thenReturn(LocalDateTime.now());
        return user;
    }
}
