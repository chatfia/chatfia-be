package com.project.chatfiabe.domain.user.entity;

import com.project.chatfiabe.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "이메일은 필수입니다")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "비밀번호는 필수입니다")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "닉네임은 필수입니다")
    @Column(nullable = false, unique = true)
    private String nickname;



    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}
