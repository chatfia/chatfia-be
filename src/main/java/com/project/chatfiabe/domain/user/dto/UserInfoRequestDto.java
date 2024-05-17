package com.project.chatfiabe.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoRequestDto {
    private String nickname;
    private String currentPassword;
    private String newPassword;
    private String newPasswordConfirm;
}
