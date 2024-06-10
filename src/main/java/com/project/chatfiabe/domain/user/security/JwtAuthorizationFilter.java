package com.project.chatfiabe.domain.user.security;

import com.project.chatfiabe.domain.user.entity.TokenType;
import com.project.chatfiabe.domain.user.jwt.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtProvider jwtProvider, UserDetailsServiceImpl userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = jwtProvider.getJwtFromHeader(request, TokenType.ACCESS);
        String refreshToken = jwtProvider.getJwtFromHeader(request, TokenType.REFRESH);

        if (StringUtils.hasText(accessToken)) {
            if (!jwtProvider.validateToken(accessToken)) {
                log.error("AccessToken Error");
                return;
            }

            Claims info = jwtProvider.getUserInfoFromToken(accessToken);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        } else if (StringUtils.hasText(refreshToken)) {
            if (!jwtProvider.validateToken(refreshToken)) {
                log.error("RefreshToken Error");
                return;
            }

            Claims info = jwtProvider.getUserInfoFromToken(refreshToken);

            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Security 에서 관리하는 인증 객체 저장소 SecurityContextHolder 에 인증 객체 저장
     * @param email : 사용자 식별값
     */
    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        if (authentication != null) {
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            log.info("Authentication set for email : {}", email);
        } else {
            log.info("Authentication creation failed for email: {}", email);
        }
    }

    /**
     * Security 에서 제공하는 UsernamePasswordAuthenticationToken 토큰 인증 객체를 생성
     * @param email : 사용자 식별값
     * @return Authentication : Security 인증 객체 (UsernamePasswordAuthenticationToken)
     */
    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        log.info("UserDetails loaded for email : {}", email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
