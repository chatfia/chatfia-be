package com.project.chatfiabe.domain.user.dto;

import com.project.chatfiabe.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserInfoResponseDto {
    private Long id;
    private String email;
    private String nickname;
    // ---- (유저 승률 데이터 필드 - 추후 추가 필요)

    public UserInfoResponseDto(User updatedUser) {
        this.id = updatedUser.getId();
        this.nickname = updatedUser.getNickname();
    }

    // 추후 승률 파라미터 추가 필요
    public UserInfoResponseDto(Long id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }
}
