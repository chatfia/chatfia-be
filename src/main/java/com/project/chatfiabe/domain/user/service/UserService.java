package com.project.chatfiabe.domain.user.service;

import com.project.chatfiabe.domain.user.dto.*;
import com.project.chatfiabe.domain.user.entity.User;

public interface UserService {
    SignupResponseDto signup(SignupRequestDto requestDto);

    UserInfoResponseDto updateUserInfo(User user, String newNickname);

    void deleteUser(User user, DeleteUserInfoRequestDto requestDto);

    void updatePassword(User user, UserInfoRequestDto requestDto);

    UserInfoResponseDto getUserInfo(User user);
}
