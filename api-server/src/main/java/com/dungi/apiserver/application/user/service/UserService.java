package com.dungi.apiserver.application.user.service;

import com.dungi.apiserver.application.user.dto.CreateSnsUserDto;
import com.dungi.apiserver.application.user.dto.CreateUserDto;
import com.dungi.apiserver.application.user.dto.SnsLoginDto;
import com.dungi.common.exception.BaseException;
import com.dungi.common.util.StringUtil;
import com.dungi.common.value.SnsProvider;
import com.dungi.core.domain.user.model.User;
import com.dungi.core.integration.file.FileUploader;
import com.dungi.core.integration.sms.SmsSender;
import com.dungi.core.integration.sns.SnsStrategy;
import com.dungi.core.integration.store.user.AuthStore;
import com.dungi.core.integration.store.user.UserStore;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dungi.common.response.BaseResponseStatus.*;
import static com.dungi.common.util.NumberUtil.CODE_DURATION;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final FileUploader fileUploader;
    private final UserStore userStore;
    private final AuthStore authStore;
    private final SmsSender smsSender;
    private final Map<SnsProvider, SnsStrategy> snsStrategyMap;

    public UserService(
            PasswordEncoder passwordEncoder,
            FileUploader fileUploader,
            UserStore userStore,
            AuthStore authStore,
            SmsSender smsSender,
            List<SnsStrategy> snsStrategyList
    ) {
        this.passwordEncoder = passwordEncoder;
        this.fileUploader = fileUploader;
        this.userStore = userStore;
        this.authStore = authStore;
        this.smsSender = smsSender;
        this.snsStrategyMap = snsStrategyList.stream()
                .collect(Collectors.toMap(SnsStrategy::getServiceType, snsStrategy -> snsStrategy));
    }

    @Transactional
    public User createUser(CreateUserDto dto) throws Exception {
        checkEmailPresent(dto.getEmail());
        String imageDownUrl = fileUploader.imageUpload(dto.getImg());
        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        var user = User.builder()
                .email(dto.getEmail())
                .password(hashedPassword)
                .name(dto.getName())
                .phoneNumber(dto.getPhoneNumber())
                .nickname(dto.getNickname())
                .profileImg(imageDownUrl)
                .snsProvider(SnsProvider.LOCAL)
                .build();
        return userStore.saveUser(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendSms(String phoneNumber) {
        String randomNumber = StringUtil.randomNumber();
        String trimmedPhoneNumber = StringUtil.trimPhoneNumber(phoneNumber);
        authStore.saveSmsCode(trimmedPhoneNumber, randomNumber, CODE_DURATION);
        smsSender.sendSms(trimmedPhoneNumber, randomNumber);
    }

    @Transactional(readOnly = true)
    public void compareCode(String code, String phoneNumber) {
        String trimmedPhoneNumber = StringUtil.trimPhoneNumber(phoneNumber);
        String savedCode = authStore.getSmsCode(trimmedPhoneNumber);
        if (!savedCode.equals(code)) {
            throw new BaseException(CODE_NOT_EQUAL);
        }
    }

    @Transactional
    public User createSnsUser(CreateSnsUserDto dto) throws Exception {
        var snsStrategy = snsStrategyMap.get(dto.getServiceType());
        String snsEmail = snsStrategy.getSnsEmail(dto.getAccessToken());
        if (!dto.getEmail().equals(snsEmail)) {
            throw new BaseException(NOT_EXISTS_EMAIL);
        }
        checkEmailPresent(dto.getEmail());
        String imageDownUrl = dto.getSnsImg();
        if (imageDownUrl == null) {
            imageDownUrl = fileUploader.imageUpload(dto.getProfileImg());
        }

        User user = User.builder()
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .profileImg(imageDownUrl)
                .snsProvider(dto.getServiceType())
                .build();
        return userStore.saveUser(user);
    }

    @Transactional(readOnly = true)
    public User login(String email, String password) {
        User user = userStore.getUserByEmail(email);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException(PASSWORD_NOT_EQUAL);
        }
        return user;
    }

    @Transactional(readOnly = true)
    public User snsLogin(SnsLoginDto dto) throws Exception {
        var snsStrategy = snsStrategyMap.get(dto.getServiceType());
        String snsEmail = snsStrategy.getSnsEmail(dto.getAccessToken());
        if (!dto.getEmail().equals(snsEmail)) {
            throw new BaseException(SNS_LOGIN_FAIL);
        }
        return userStore.getUserByEmail(dto.getEmail());
    }

    public String snsToken(String code, SnsProvider serviceType) throws Exception {
        var snsStrategy = snsStrategyMap.get(serviceType);
        return snsStrategy.getSnsToken(code);
    }

    public void checkEmailPresent(String email) {
        userStore.checkEmailPresent(email);
    }
}
