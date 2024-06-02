package com.project.chatfiabe.global.websocket.controller;

import com.project.chatfiabe.global.websocket.dto.ChatMessage;
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
        // 메시지 타입을 CHAT으로 설정
        chatMessage.setType(ChatMessage.MessageType.CHAT);
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
        headerAccessor.getSessionAttributes().put("nickname", chatMessage.getSender());

        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setContent(chatMessage.getSender() + "님이 방에 참여하셨습니다.");
        return chatMessage;
    }

    /**
     * 클라이언트가 /app/chat.leaveUser로 메시지를 보내면,
     * /topic/public 채널로 유저 떠남 메시지를 전송합니다.
     *
     * @param chatMessage 클라이언트로부터 받은 채팅 메시지
     * @param headerAccessor WebSocket 세션에 접근할 수 있는 객체
     * @return 처리된 채팅 메시지
     */
    @MessageMapping("/chat.leaveUser")
    @SendTo("/topic/public")
    public ChatMessage leaveUser(@Payload ChatMessage chatMessage,
                                 SimpMessageHeaderAccessor headerAccessor) {
        // WebSocket 세션에서 유저 이름을 제거
        headerAccessor.getSessionAttributes().remove("nickname");

        chatMessage.setType(ChatMessage.MessageType.LEAVE);
        chatMessage.setContent(chatMessage.getSender() + "님이 방에서 나가셨습니다.");
        return chatMessage;
    }
}
