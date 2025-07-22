package com.dungi.core.integration.store.user;

import com.dungi.core.domain.user.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStore {
    void saveUser(User user);

    void checkEmailPresent(String email);

    User getUserByEmail(String email);

    User getUserById(Long id);

    void saveCode(String number, String code, long time);

    String getCode(String number);

    void saveToken(String token, String email, long time);

    String getInfo(String token);
}
