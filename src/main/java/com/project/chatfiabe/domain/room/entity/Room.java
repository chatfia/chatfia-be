package com.project.chatfiabe.domain.room.entity;

import com.project.chatfiabe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomName;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

//    @ManyToOne
//    @Join
//    private List<User> participants = new ArrayList<>();
}
