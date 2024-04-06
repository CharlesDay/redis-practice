package com.charlie.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class ListRepository {
    final RedisTemplate<Long, Map> redisTemplate;

    public void save(long id, Map list) {
        redisTemplate.opsForValue().set(id, list);
    }

    public void lock(long id, Map list) {
        redisTemplate.opsForValue().setIfAbsent(id, list, 10, TimeUnit.SECONDS);

    }

    public Map findById(Long id) {
        return redisTemplate.opsForValue().get(id);
    }

    public Boolean evict(Long id) {
        return redisTemplate.delete(id);
    }
}
