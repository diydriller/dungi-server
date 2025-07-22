package com.dungi.storage.redis.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate redisTemplate;

    public void saveString(String k, String v, long time) {
        redisTemplate.opsForValue().set(k, v, time, TimeUnit.MILLISECONDS);
    }

    public Optional<String> getString(String k) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(k).toString());
    }
}
