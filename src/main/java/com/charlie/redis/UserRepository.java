package com.charlie.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;


@Repository
@RequiredArgsConstructor
public class UserRepository {
    final RedisTemplate<Long, User> redisTemplate;

    public void save(User user) {
        redisTemplate.opsForValue().set(user.getId(), user);
    }

    public void lock(User user) {
        redisTemplate.opsForValue().setIfAbsent(user.getId(), user, 10, TimeUnit.SECONDS);

    }

    public void saveAll(Set<User> userSet) {
        for (User user : userSet) {
            save(user);
        }
    }

    public User findById(Long id) {
        return redisTemplate.opsForValue().get(id);
    }

    public Boolean evict(Long id) {
        return redisTemplate.delete(id);
    }
}
