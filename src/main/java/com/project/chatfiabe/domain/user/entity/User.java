package com.project.chatfiabe.domain.user.entity;

import com.project.chatfiabe.domain.room.entity.Room;
import com.project.chatfiabe.domain.user.jwt.dto.JwtTokenInfo;
import com.project.chatfiabe.global.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.project.chatfiabe.domain.user.jwt.util.DateTimeUtil.convertToLocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UserType userType;

    @Column(length = 250)
    private String accessToken;

    private LocalDateTime accessTokenExpirationTime;

    @Column(length = 250)
    private String refreshToken;

    private LocalDateTime refreshTokenExpirationTime;

    private int wins;

    private int losses;

    @ManyToOne
    private Room room;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AccessLog> accessLogs;

    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.wins = 0;
        this.losses = 0;
    }

    public void updateRefreshTokenInfo(JwtTokenInfo.RefreshTokenInfo refreshTokenInfo) {
        this.refreshToken = refreshTokenInfo.getRefreshToken();
        this.refreshTokenExpirationTime = convertToLocalDateTime(refreshTokenInfo.getRefreshTokenExpireTime());
    }

    public void updateAccessTokenInfo(JwtTokenInfo.AccessTokenInfo accessTokenInfo) {
        this.accessToken = accessTokenInfo.getAccessToken();
        this.accessTokenExpirationTime = convertToLocalDateTime(accessTokenInfo.getAccessTokenExpireTime());
    }

    public void expireRefreshTokenExpirationTime(LocalDateTime now) {
        this.refreshTokenExpirationTime = now;
    }

    public void expireAccessTokenExpirationTime(LocalDateTime now) {
        this.accessTokenExpirationTime = now;
    }


    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    // 유저 승률
    public double getWinRate() {
        int totalGames = wins + losses;
        return totalGames == 0 ? 0 : (double) wins / totalGames;
    }

    // 유저 승리 횟수+
    public void plusWins() {
        this.wins++;
    }

    // 유저 패배 횟수+
    public void plusLosses() {
        this.losses++;
    }
}
