package com.dungi.apiserver.application.user.service;

import com.dungi.apiserver.application.user.dto.CreateSnsUserDto;
import com.dungi.apiserver.application.user.dto.CreateUserDto;
import com.dungi.apiserver.application.user.dto.SnsLoginDto;
import com.dungi.common.exception.BaseException;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.common.value.SnsProvider;
import com.dungi.core.domain.user.model.User;
import com.dungi.core.integration.file.FileUploader;
import com.dungi.core.integration.sms.SmsSender;
import com.dungi.core.integration.sns.SnsStrategy;
import com.dungi.core.integration.store.user.AuthStore;
import com.dungi.core.integration.store.user.UserStore;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;

import static com.dungi.common.response.BaseResponseStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FileUploader fileUploader;

    @Mock
    private UserStore userStore;

    @Mock
    private AuthStore authStore;

    @Mock
    private SmsSender smsSender;

    @Mock
    private SnsStrategy snsStrategy;

    @Mock
    private Map<SnsProvider, SnsStrategy> snsStrategyMap;

    private UserService userService;

    private final String EMAIL = "test@example.com";
    private final String PASSWORD = "password123";
    private final String NAME = "홍길동";
    private final String PHONE_NUMBER = "010-1234-5678";
    private final String NICKNAME = "테스트유저";
    private final String PROFILE_IMG = "https://example.com/profile.jpg";
    private final String ACCESS_TOKEN = "access_token_123";

    @BeforeEach
    void setUp() {
        when(snsStrategy.getServiceType()).thenReturn(SnsProvider.KAKAO);
        List<SnsStrategy> snsStrategyList = List.of(snsStrategy);
        userService = new UserService(passwordEncoder, fileUploader, userStore, authStore, smsSender, snsStrategyList);

        try {
            java.lang.reflect.Field field = UserService.class.getDeclaredField("snsStrategyMap");
            field.setAccessible(true);
            field.set(userService, snsStrategyMap);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set snsStrategyMap field", e);
        }
        
        lenient().when(snsStrategyMap.get(SnsProvider.KAKAO)).thenReturn(snsStrategy);
    }

    @Test
    @DisplayName("일반 회원가입 성공")
    void createUser_Success() throws Exception {
        // given
        CreateUserDto dto = CreateUserDto.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .name(NAME)
                .phoneNumber(PHONE_NUMBER)
                .nickname(NICKNAME)
                .build();

        User savedUser = createMockUser();
        when(fileUploader.imageUpload(any())).thenReturn(PROFILE_IMG);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userStore.saveUser(any(User.class))).thenReturn(savedUser);

        // when
        User result = userService.createUser(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getName()).isEqualTo(NAME);
        assertThat(result.getNickname()).isEqualTo(NICKNAME);

        verify(userStore).checkEmailPresent(EMAIL);
        verify(fileUploader).imageUpload(any());
        verify(passwordEncoder).encode(PASSWORD);
        verify(userStore).saveUser(any(User.class));
    }

    @Test
    @DisplayName("SNS 회원가입 성공")
    void createSnsUser_Success() throws Exception {
        // given
        CreateSnsUserDto dto = CreateSnsUserDto.builder()
                .email(EMAIL)
                .nickname(NICKNAME)
                .snsImg(PROFILE_IMG)
                .serviceType(SnsProvider.KAKAO)
                .accessToken(ACCESS_TOKEN)
                .build();

        User savedUser = createMockSnsUser();
        when(snsStrategy.getSnsEmail(ACCESS_TOKEN)).thenReturn(EMAIL);
        when(userStore.saveUser(any(User.class))).thenReturn(savedUser);

        // when
        User result = userService.createSnsUser(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getNickname()).isEqualTo(NICKNAME);
        assertThat(result.getSnsProvider()).isEqualTo(SnsProvider.KAKAO);

        verify(snsStrategy).getSnsEmail(ACCESS_TOKEN);
        verify(userStore).checkEmailPresent(EMAIL);
        verify(userStore).saveUser(any(User.class));
    }

    @Test
    @DisplayName("SNS 회원가입 실패 - 이메일 불일치")
    void createSnsUser_Fail_EmailMismatch() throws Exception {
        // given
        CreateSnsUserDto dto = CreateSnsUserDto.builder()
                .email(EMAIL)
                .nickname(NICKNAME)
                .snsImg(PROFILE_IMG)
                .serviceType(SnsProvider.KAKAO)
                .accessToken(ACCESS_TOKEN)
                .build();

        when(snsStrategy.getSnsEmail(ACCESS_TOKEN)).thenReturn("different@example.com");

        // when & then
        assertThatThrownBy(() -> userService.createSnsUser(dto))
                .isInstanceOf(BaseException.class)
                .extracting("status", as(InstanceOfAssertFactories.type(BaseResponseStatus.class)))
                .extracting(BaseResponseStatus::getMessage, InstanceOfAssertFactories.STRING)
                .isEqualTo(NOT_EXISTS_EMAIL.getMessage());

        verify(snsStrategy).getSnsEmail(ACCESS_TOKEN);
        verify(userStore, never()).saveUser(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_Success() {
        // given
        User user = createMockUser();
        when(userStore.getUserByEmail(EMAIL)).thenReturn(user);
        when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(true);

        // when
        User result = userService.login(EMAIL, PASSWORD);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(EMAIL);

        verify(userStore).getUserByEmail(EMAIL);
        verify(passwordEncoder).matches(PASSWORD, user.getPassword());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_Fail_WrongPassword() {
        // given
        User user = createMockUser();
        when(userStore.getUserByEmail(EMAIL)).thenReturn(user);
        when(passwordEncoder.matches(PASSWORD, user.getPassword())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.login(EMAIL, PASSWORD))
                .isInstanceOf(BaseException.class)
                .extracting("status", as(InstanceOfAssertFactories.type(BaseResponseStatus.class)))
                .extracting(BaseResponseStatus::getMessage, InstanceOfAssertFactories.STRING)
                .isEqualTo(PASSWORD_NOT_EQUAL.getMessage());

        verify(userStore).getUserByEmail(EMAIL);
        verify(passwordEncoder).matches(PASSWORD, user.getPassword());
    }

    @Test
    @DisplayName("SNS 로그인 성공")
    void snsLogin_Success() throws Exception {
        // given
        SnsLoginDto dto = SnsLoginDto.builder()
                .email(EMAIL)
                .serviceType(SnsProvider.KAKAO)
                .accessToken(ACCESS_TOKEN)
                .build();

        User user = createMockSnsUser();
        when(snsStrategy.getSnsEmail(ACCESS_TOKEN)).thenReturn(EMAIL);
        when(userStore.getUserByEmail(EMAIL)).thenReturn(user);

        // when
        User result = userService.snsLogin(dto);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(EMAIL);

        verify(snsStrategy).getSnsEmail(ACCESS_TOKEN);
        verify(userStore).getUserByEmail(EMAIL);
    }

    @Test
    @DisplayName("SNS 로그인 실패 - 이메일 불일치")
    void snsLogin_Fail_EmailMismatch() throws Exception {
        // given
        SnsLoginDto dto = SnsLoginDto.builder()
                .email(EMAIL)
                .serviceType(SnsProvider.KAKAO)
                .accessToken(ACCESS_TOKEN)
                .build();

        when(snsStrategy.getSnsEmail(ACCESS_TOKEN)).thenReturn("different@example.com");

        // when & then
        assertThatThrownBy(() -> userService.snsLogin(dto))
                .isInstanceOf(BaseException.class)
                .extracting("status", as(InstanceOfAssertFactories.type(BaseResponseStatus.class)))
                .extracting(BaseResponseStatus::getMessage, InstanceOfAssertFactories.STRING)
                .isEqualTo(SNS_LOGIN_FAIL.getMessage());

        verify(snsStrategy).getSnsEmail(ACCESS_TOKEN);
        verify(userStore, never()).getUserByEmail(anyString());
    }

    @Test
    @DisplayName("SMS 전송 성공")
    void sendSms_Success() {
        // given
        String phoneNumber = "010-1234-5678";
        String trimmedPhoneNumber = "+8210-1234-5678";

        doNothing().when(authStore).saveSmsCode(anyString(), anyString(), anyLong());

        // when
        userService.sendSms(phoneNumber);

        // then
        verify(authStore).saveSmsCode(eq(trimmedPhoneNumber), anyString(), anyLong());
        verify(smsSender).sendSms(eq(trimmedPhoneNumber), anyString());
    }

    @Test
    @DisplayName("인증번호 확인 성공")
    void compareCode_Success() {
        // given
        String code = "123456";
        String phoneNumber = "010-1234-5678";
        String trimmedPhoneNumber = "+8210-1234-5678";

        when(authStore.getSmsCode(trimmedPhoneNumber)).thenReturn(code);

        // when
        userService.compareCode(code, phoneNumber);

        // then
        verify(authStore).getSmsCode(trimmedPhoneNumber);
    }

    @Test
    @DisplayName("인증번호 확인 실패 - 코드 불일치")
    void compareCode_Fail_CodeMismatch() {
        // given
        String code = "123456";
        String phoneNumber = "010-1234-5678";
        String trimmedPhoneNumber = "+8210-1234-5678";

        when(authStore.getSmsCode(trimmedPhoneNumber)).thenReturn("654321");

        // when & then
        assertThatThrownBy(() -> userService.compareCode(code, phoneNumber))
                .isInstanceOf(BaseException.class)
                .extracting("status", as(InstanceOfAssertFactories.type(BaseResponseStatus.class)))
                .extracting(BaseResponseStatus::getMessage, InstanceOfAssertFactories.STRING)
                .isEqualTo(CODE_NOT_EQUAL.getMessage());

        verify(authStore).getSmsCode(trimmedPhoneNumber);
    }

    private User createMockUser() {
        User user = mock(User.class);
        lenient().when(user.getId()).thenReturn(1L);
        lenient().when(user.getEmail()).thenReturn(EMAIL);
        lenient().when(user.getPassword()).thenReturn(PASSWORD);
        lenient().when(user.getName()).thenReturn(NAME);
        lenient().when(user.getNickname()).thenReturn(NICKNAME);
        return user;
    }

    private User createMockSnsUser() {
        User user = mock(User.class);
        lenient().when(user.getId()).thenReturn(1L);
        lenient().when(user.getEmail()).thenReturn(EMAIL);
        lenient().when(user.getPassword()).thenReturn(PASSWORD);
        lenient().when(user.getName()).thenReturn(NAME);
        lenient().when(user.getNickname()).thenReturn(NICKNAME);
        lenient().when(user.getSnsProvider()).thenReturn(SnsProvider.KAKAO);
        return user;
    }
}
