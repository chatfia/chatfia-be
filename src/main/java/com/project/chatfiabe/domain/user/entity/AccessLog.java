package com.project.chatfiabe.domain.user.entity;

import com.project.chatfiabe.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class AccessLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512)
    private String ua;

    @Column
    private String endpoint;

    @Column
    private String ip;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public AccessLog() {}

    public AccessLog(String ua, String endpoint, String ip, User user) {
        this.ua = ua;
        this.endpoint = endpoint;
        this.ip = ip;
        this.user = user;
    }
}