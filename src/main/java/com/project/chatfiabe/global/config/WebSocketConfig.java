package com.project.chatfiabe.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 구독할 수 있는 메시지 브로커를 설정
        config.enableSimpleBroker("/topic");
        // 클라이언트가 메시지를 보낼 때 사용할 애플리케이션 목적지를 설정
        config.setApplicationDestinationPrefixes("/app");
    }

    // STOMP 엔드포인트 등록
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /ws 엔드포인트를 통해 WebSocket을 사용할 수 있도록 설정
        // SockJS를 사용하여 WebSocket을 지원하지 않는 브라우저에서도 WebSocket을 사용할 수 있도록 설정
        registry.addEndpoint("/ws").withSockJS();
    }
}
