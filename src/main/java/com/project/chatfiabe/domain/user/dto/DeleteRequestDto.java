package com.project.chatfiabe.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteRequestDto {

    /**
     * 하나 이상의 대문자(A-Z) 포함
     * 하나 이상의 소문자(a-z) 포함
     * 하나 이상의 숫자(0-9) 포함
     * 하나 이상의 특수문자(@$!%*?_~(),) 포함
     * 8에서 15 사이 총 길이
     */
    @NotBlank(message = "비밀번호가 입력되지 않았습니다. 비밀번호를 입력해 주세요.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?_~(),])[A-Za-z\\d@$!%*?_~(),]{8,15}$",
            message = "비밀번호는 8자에서 15자 사이의 알파벳 대소문자, 숫자, 특수문자(@$!%*?_~(),)로 구성되어야 합니다.")
    private String password;

    public DeleteRequestDto(String password) {
        this.password = password;
    }
}
