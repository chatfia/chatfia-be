package com.project.chatfiabe.domain.user.service.impl;

import com.project.chatfiabe.domain.user.entity.TokenType;
import com.project.chatfiabe.domain.user.entity.User;
import com.project.chatfiabe.domain.user.jwt.JwtProvider;
import com.project.chatfiabe.domain.user.repository.UserRepository;
import com.project.chatfiabe.domain.user.service.AuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public String refreshAccessToken(String refreshToken) {
        // refreshToken 유효성 검사
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid Token");
        }
        // Jwt Claims
        Claims info = jwtProvider.getUserInfoFromToken(refreshToken);

        // User 조회
        User user = userRepository.findByEmail(info.getSubject()).orElseThrow(
                () -> new RuntimeException("Not Found User By : " + info.getSubject())
        );

        return jwtProvider.createToken(jwtProvider.createTokenPayload(user.getEmail(), TokenType.ACCESS));
    }
}
