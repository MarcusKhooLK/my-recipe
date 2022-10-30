package com.myrecipe.server.repository;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SessionRepository {
    
    public static final String MAP_NAME = "session_map";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public void setSession(String key, String value) {
        redisTemplate.opsForHash().put(MAP_NAME, key, value);
        redisTemplate.expire(MAP_NAME, Duration.ofSeconds(86400));
    }

    public boolean hasSession(String key) {
        return redisTemplate.opsForHash().hasKey(MAP_NAME, key);
    }

    public void removeSession(String key) { 
        redisTemplate.opsForHash().delete(MAP_NAME, key);
    }

    public String getValue(String key) {
        return (String)redisTemplate.opsForHash().get(MAP_NAME, key);
    }
}
