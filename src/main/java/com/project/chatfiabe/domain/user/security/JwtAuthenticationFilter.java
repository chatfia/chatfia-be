package com.project.chatfiabe.domain.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chatfiabe.domain.user.repository.AccessTokenRepository;
import com.project.chatfiabe.domain.user.dto.LoginRequestDto;
import com.project.chatfiabe.domain.user.entity.*;
import com.project.chatfiabe.domain.user.jwt.JwtProvider;
import com.project.chatfiabe.domain.user.jwt.TokenPayload;
import com.project.chatfiabe.domain.user.repository.AccessLogRepository;
import com.project.chatfiabe.domain.user.repository.RefreshTokenRepository;
import com.project.chatfiabe.global.util.HttpRequestUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtProvider jwtProvider;
    private final AccessLogRepository accessLogRepository;

    private final AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, AccessLogRepository accessLogRepository, AccessTokenRepository accessTokenRepository, RefreshTokenRepository refreshTokenRepository) {
        this.jwtProvider = jwtProvider;
        this.accessLogRepository = accessLogRepository;
        this.accessTokenRepository = accessTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginRequestDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginRequestDto.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getEmail(),
                            requestDto.getPassword(),
                            null
                    )
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Login 성공
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        User user = ((UserDetailsImpl) authResult.getPrincipal()).getUser();
        String email = user.getEmail();

        // 액세스 토큰 페이로드 생성
        TokenPayload accessTokenPayload = jwtProvider.createTokenPayload(user.getEmail(), TokenType.ACCESS);
        String accessTokenValue = jwtProvider.createToken(accessTokenPayload);

        // 리프레시 토큰 페이로드 생성
        TokenPayload refreshTokenPayload = jwtProvider.createTokenPayload(user.getEmail(), TokenType.REFRESH);
        String refreshTokenValue = jwtProvider.createToken(refreshTokenPayload);

        // AccessToken 엔티티 생성 및 저장
        AccessToken accessToken = accessTokenRepository.findByUserId(user.getId())
                .orElseGet(() -> AccessToken.builder().user(user).build());
        accessToken.updateTokenInfo(accessTokenValue, accessTokenPayload.getJti(), accessTokenPayload.getExpiresAt());
        accessTokenRepository.save(accessToken);

        // RefreshToken 엔티티 생성 및 저장
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId())
                .orElseGet(() -> RefreshToken.builder().user(user).build());
        refreshToken.updateTokenInfo(refreshTokenValue, refreshTokenPayload.getJti(), refreshTokenPayload.getExpiresAt());
        refreshTokenRepository.save(refreshToken);

        // response 반환
        response.addHeader(JwtProvider.ACCESS_TOKEN_HEADER, accessTokenValue);
        response.addHeader(JwtProvider.REFRESH_TOKEN_HEADER, refreshTokenValue);

        AccessLog accessLog = new AccessLog(
                HttpRequestUtils.getUserAgent(request),
                request.getRequestURI(),
                HttpRequestUtils.getRemoteAddr(request),
                user
        );
        accessLogRepository.save(accessLog);
    }

    /**
     * Login 실패
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
