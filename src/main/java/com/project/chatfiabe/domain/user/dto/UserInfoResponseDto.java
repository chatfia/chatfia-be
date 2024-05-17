package com.project.chatfiabe.domain.user.dto;

import com.project.chatfiabe.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserInfoResponseDto {
    private Long id;
    private String nickname;

    public UserInfoResponseDto(User updatedUser) {
        this.id = updatedUser.getId();
        this.nickname = updatedUser.getNickname();
    }
}
