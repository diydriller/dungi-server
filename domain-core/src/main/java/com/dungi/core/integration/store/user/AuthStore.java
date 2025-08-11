package com.dungi.core.integration.store.user;

import org.springframework.stereotype.Repository;

@Repository
public interface AuthStore {
    void saveSmsCode(String number, String code, long time);

    String getSmsCode(String number);

    void saveRefreshToken(String token, String email, long time);

    String validateRefreshTokenAndGetEmail(String token);
}
