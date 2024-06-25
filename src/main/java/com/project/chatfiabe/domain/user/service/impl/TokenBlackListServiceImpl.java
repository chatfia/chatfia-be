package com.project.chatfiabe.domain.user.service.impl;

import com.project.chatfiabe.domain.user.entity.TokenBlackList;
import com.project.chatfiabe.domain.user.jwt.constant.TokenType;
import com.project.chatfiabe.domain.user.jwt.util.JwtTokenUtil;
import com.project.chatfiabe.domain.user.repository.TokenBlackListRepository;
import com.project.chatfiabe.domain.user.service.TokenBlackListService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenBlackListServiceImpl implements TokenBlackListService {
    private final JwtTokenUtil jwtTokenUtil;
    private final TokenBlackListRepository tokenBlackListRepository;

    @Override
    @Transactional
    public void addToBlackList(String accessToken, String refreshToken) {
        Claims accessClaims = jwtTokenUtil.getUserInfoFromToken(accessToken);
        Claims refreshClaims = jwtTokenUtil.getUserInfoFromToken(refreshToken);

        tokenBlackListRepository.save(new TokenBlackList(
                accessToken,
                accessClaims.getId(),
                TokenType.ACCESS,
                accessClaims.getExpiration()
        ));

        tokenBlackListRepository.save(new TokenBlackList(
                refreshToken,
                refreshClaims.getId(),
                TokenType.REFRESH,
                refreshClaims.getExpiration()
        ));
    }

    @Override
    @Transactional
    public boolean isTokenBlacklisted(String jti) {
        Optional<TokenBlackList> tokenByJti = tokenBlackListRepository.findByJti(jti);
        return tokenByJti.isPresent();
    }

    @Override
    @Transactional
    public void removeExpiredTokens() {
        List<TokenBlackList> expiredList = tokenBlackListRepository.findAllByExpiresAtLessThan(new Date());
        tokenBlackListRepository.deleteAllInBatch(expiredList);
    }
}
