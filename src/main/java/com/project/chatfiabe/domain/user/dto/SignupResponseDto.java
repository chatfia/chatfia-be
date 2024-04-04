package com.project.chatfiabe.domain.user.dto;

import lombok.Getter;

@Getter
public class SignupResponseDto {
    private Long id;
    private String email;
    private String password;
    private String nickname;

    public SignupResponseDto(Long id, String email, String password, String nickname) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
