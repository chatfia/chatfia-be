package com.project.chatfiabe.global.mail.service;

public interface RedisService {
    void setCode(String email, String code);
    String getCode(String email);
}
