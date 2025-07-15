package cn.aicamera.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void storeToken(String email, String token) {
        redisTemplate.opsForValue().set(email, token);
    }

    public void removeToken(String username) {
        redisTemplate.delete(username);
    }

    public String getToken(String username) {
        return redisTemplate.opsForValue().get(username);
    }
}