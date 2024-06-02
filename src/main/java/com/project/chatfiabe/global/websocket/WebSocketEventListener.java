package com.project.chatfiabe.global.websocket;

import com.project.chatfiabe.domain.game.entity.Game;
import com.project.chatfiabe.domain.game.repository.GameRepository;
import com.project.chatfiabe.domain.game.service.GameService;
import com.project.chatfiabe.domain.room.entity.Room;
import com.project.chatfiabe.domain.room.repository.RoomRepository;
import com.project.chatfiabe.domain.user.entity.User;
import com.project.chatfiabe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String nickname = (String) headerAccessor.getSessionAttributes().get("nickname");

        if (nickname != null) {
            User user = userRepository.findByNickname(nickname)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Room room = user.getRoom();

            if (room != null) {
                room.removePlayer(user);
                roomRepository.save(room);

                // 만약 방장이라면 새로운 방장 지정
                if (room.getHostId().equals(user.getId())) {
                    if (!room.getPlayers().isEmpty()) {
                        room.setHostId(room.getPlayers().get(0).getId());
                    } else {
                        // 방에 더 이상 사용자가 없으면 방 삭제
                        roomRepository.delete(room);
                    }
                }

                // 사용자 퇴장을 다른 사용자에게 알림
                String message = nickname + "님이 연결이 종료되어 방에서 나갔습니다.";
                messagingTemplate.convertAndSend("/topic/room/" + room.getId(), message);

                // 게임 중이라면 게임 상태 업데이트
                Optional<Game> gameOptional = gameRepository.findByRoomId(room.getId());
                gameOptional.ifPresent(game -> {
                    if (game.getState() != Game.GameState.ENDED) {
                        gameService.checkVictoryConditions(game);
                    }
                });
            }

            // 사용자 상태를 오프라인으로 변경
            user.setRoom(null);
            userRepository.save(user);
        }
    }
}
