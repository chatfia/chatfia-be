package com.project.chatfiabe.global.controller;

import com.project.chatfiabe.global.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {


    /**
     * 클라이언트가 /app/chat.sendMessage로 메시지를 보내면,
     * /topic/public 채널로 해당 메시지를 전송
     *
     * @param chatMessage 클라이언트로부터 받은 채팅 메시지
     * @return 처리된 채팅 메시지
     */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }


    /**
     * 클라이언트가 /app/chat.addUser로 메시지를 보내면,
     * /topic/public 채널로 유저 참여 메시지를 전송
     *
     * @param chatMessage 클라이언트로부터 받은 채팅 메시지
     * @param headerAccessor WebSocket 세션에 접근할 수 있는 객체
     * @return 처리된 채팅 메시지
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        // WebSocket 세션에 유저 이름 저장
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}
