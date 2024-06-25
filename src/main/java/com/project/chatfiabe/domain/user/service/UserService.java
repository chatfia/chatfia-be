package com.project.chatfiabe.domain.user.service;

import com.project.chatfiabe.domain.user.dto.*;
import com.project.chatfiabe.domain.user.entity.User;
import com.project.chatfiabe.domain.user.jwt.dto.JwtTokenInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface UserService {
    void registerAccount(SignupRequestDto signupRequestDto, BindingResult bindingResult) throws MethodArgumentNotValidException;

    void deleteAccount(User user, String inputPassword, HttpServletResponse httpServletResponse);

    void logout(User user, HttpServletResponse httpServletResponse);

    UserInfoResponseDto updateUserInfo(User user, String newNickname);

    void updatePassword(User user, UserInfoRequestDto requestDto);

    UserInfoResponseDto getUserInfo(User user);

    void updateRefreshToken(User user, JwtTokenInfo.RefreshTokenInfo refreshTokenInfo);

    void updateAccessToken(User user, JwtTokenInfo.AccessTokenInfo accessTokenInfo);

    User findUserByAccessToken(String accessToken);

    boolean isRefreshTokenValid(String refreshToken);
}
