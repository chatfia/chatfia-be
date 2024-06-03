package com.project.chatfiabe.global.mail.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MailResponseDto {
    private String code;

    public MailResponseDto(String code) {
        this.code = code;
    }
}
