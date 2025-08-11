package com.dungi.storage.store.user;

import com.dungi.common.exception.BaseException;
import com.dungi.common.response.BaseResponseStatus;
import com.dungi.core.domain.user.model.User;
import com.dungi.core.integration.store.user.UserStore;
import com.dungi.storage.rdb.repository.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStoreImpl implements UserStore {
    private final UserJpaRepository userJpaRepository;

    @Override
    public void saveUser(User user) {
        userJpaRepository.save(user);
    }

    @Override
    public void checkEmailPresent(String email) {
        userJpaRepository.checkUserByEmail(email)
                .ifPresent(m -> {
                    throw new BaseException(BaseResponseStatus.ALREADY_EXISTS_EMAIL);
                });
    }

    @Override
    public User getUserByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_EXISTS_EMAIL));
    }

    @Override
    public User getUserById(Long id) {
        return userJpaRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_EXIST_USER));
    }
}
