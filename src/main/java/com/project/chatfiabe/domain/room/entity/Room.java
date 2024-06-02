package com.project.chatfiabe.domain.room.entity;

import com.project.chatfiabe.domain.user.entity.User;
import com.project.chatfiabe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Room extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private Long hostId;

    private int maxPlayers = 6;

    private boolean isPrivate;

    private String password;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> players = new ArrayList<>();

    public Room(String name, Long id, boolean aPrivate, String password) {
        this.name = name;
        this.hostId = id;
        this.isPrivate = aPrivate;
        this.password =  isPrivate ? password : null;
    }

    // 플레이어 방에 추가
    public void addPlayer(User user) {
        if (players.size() < maxPlayers) {
            players.add(user);
            user.setRoom(this);
        } else {
            throw new RuntimeException("Room is full");
        }
    }


    // 플레이어 방에서 제거
    public void removePlayer(User user) {
        players.remove(user);
        user.setRoom(null);
        if (user.getId().equals(hostId) && !players.isEmpty()) {
            // 방장이 나갈 경우 새로운 방장에게 방장 위임
            this.hostId = players.get(0).getId();
        }
    }

    public int getNumberOfPlayer() {
        return players.size();
    }
}
