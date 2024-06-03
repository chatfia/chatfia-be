package com.project.chatfiabe.global.mail.service.impl;

import com.project.chatfiabe.global.mail.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    public void setCode(String email, String code) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // 만료기간 5분
        valueOperations.set(email, code, 300, TimeUnit.SECONDS);
    }

    @Override
    public String getCode(String email) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object code = valueOperations.get(email);
        if (code == null) {
            throw new IllegalArgumentException("인증코드가 만료되었거나, 인증할 수 없습니다.");
        }
        return code.toString();
    }
}
