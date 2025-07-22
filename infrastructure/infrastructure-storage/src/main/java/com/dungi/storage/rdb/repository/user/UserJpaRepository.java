package com.dungi.storage.rdb.repository.user;

import com.dungi.core.domain.user.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserJpaRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u.id" +
            " FROM User u" +
            " WHERE u.email=:email")
    Optional<Long> checkUserByEmail(String email);
}