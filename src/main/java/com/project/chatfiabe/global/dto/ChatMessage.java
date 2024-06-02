package com.project.chatfiabe.global.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    private String sender;
    private String content;
    private TrayIcon.MessageType type;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
