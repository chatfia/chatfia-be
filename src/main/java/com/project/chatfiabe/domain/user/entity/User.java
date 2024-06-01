package com.project.chatfiabe.domain.user.entity;

import com.project.chatfiabe.domain.room.entity.Room;
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

    private int wins;

    private int losses;

    @ManyToOne
    private Room room;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AccessToken> accessTokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RefreshToken> refreshTokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AccessLog> accessLogs;

    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
