package com.dungi.storage.store.user;

import com.dungi.common.exception.BaseException;
import com.dungi.core.integration.store.user.AuthStore;
import com.dungi.storage.redis.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.dungi.common.response.BaseResponseStatus.AUTHORIZATION_ERROR;
import static com.dungi.common.response.BaseResponseStatus.CODE_NOT_EXIST;

@Component
@RequiredArgsConstructor
public class AuthStoreImpl implements AuthStore {
    private final RedisRepository redisRepository;

    @Override
    public void saveSmsCode(String number, String code, long time) {
        redisRepository.saveString(number, code, time);
    }

    @Override
    public String getSmsCode(String number) {
        return redisRepository.getString(number)
                .orElseThrow(() -> new BaseException(CODE_NOT_EXIST));
    }

    @Override
    public void saveRefreshToken(String token, String email, long time) {
        redisRepository.saveString(token, email, time);
    }

    @Override
    public String validateRefreshTokenAndGetEmail(String token) {
        return redisRepository.getString(token)
                .orElseThrow(() -> new BaseException(AUTHORIZATION_ERROR));
    }
}
