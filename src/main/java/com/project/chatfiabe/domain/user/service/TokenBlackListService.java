package com.project.chatfiabe.domain.user.service;

public interface TokenBlackListService {
    void addToBlackList(String accessToken, String refreshToken);

    boolean isTokenBlacklisted(String jti);

    void removeExpiredTokens();
}
