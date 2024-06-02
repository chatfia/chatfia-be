package com.project.chatfiabe.domain.game.service.impl;

import com.project.chatfiabe.domain.game.dto.GameResponseDto;
import com.project.chatfiabe.domain.game.entity.Game;
import com.project.chatfiabe.domain.game.repository.GameRepository;
import com.project.chatfiabe.domain.game.service.GameService;
import com.project.chatfiabe.domain.room.entity.Room;
import com.project.chatfiabe.domain.room.repository.RoomRepository;
import com.project.chatfiabe.domain.user.entity.User;
import com.project.chatfiabe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final RoomRepository roomRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public GameResponseDto startGame(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다."));

        if (room.getPlayers().size() != 6) {
            throw new RuntimeException("게임은 6명이 되어야 시작할 수 있습니다.");
        }

        Game game = Game.builder()
                .room(room)
                .mafiaIds(new ArrayList<>())
                .citizenIds(new ArrayList<>())
                .doctorId(null)
                .policeId(null)
                .build();

        assignRoles(game);
        game = gameRepository.save(game);

        return convertToDto(game);
    }

    @Override
    @Transactional
    public void assignRoles(Game game) {
        List<User> players = game.getRoom().getPlayers();
        Collections.shuffle(players);

        game.getMafiaIds().addAll(players.subList(0, 2).stream().map(User::getId).toList());
        game.getCitizenIds().addAll(players.subList(2, 4).stream().map(User::getId).toList());
        game.setDoctorId(players.get(4).getId());
        game.setPoliceId(players.get(5).getId());

        gameRepository.save(game);
    }

    @Override
    @Transactional
    public GameResponseDto proceedToNextPhase(Long gameId) {
        Game game = findById(gameId);

        if (game.getState() == Game.GameState.DAY) {
            game.setState(Game.GameState.NIGHT);
        } else if (game.getState() == Game.GameState.NIGHT) {
            announceNightResults(gameId);
            game.setState(Game.GameState.DAY);
        }

        gameRepository.save(game);
        checkVictoryConditions(game);

        // 변경된 게임 상태를 클라이언트에게 전송
        messagingTemplate.convertAndSend("/topic/game/" + gameId, game.getState().name() + "이 되었습니다.");

        return convertToDto(game);
    }

    @Override
    @Transactional
    public void endGame(Game game) {
        game.setState(Game.GameState.ENDED);
        gameRepository.save(game);

        Room room = game.getRoom();
        room.getPlayers().clear();
        roomRepository.save(room);

        checkVictoryConditions(game);
    }

    @Override
    @Transactional
    public void checkVictoryConditions(Game game) {
        List<Long> mafiaIds = game.getMafiaIds();
        List<Long> citizenIds = game.getCitizenIds();
        Long doctorId = game.getDoctorId();
        Long policeId = game.getPoliceId();

        long mafiaCount = mafiaIds.stream().filter(id -> !game.isPlayerDead(id)).count();
        long citizenCount = citizenIds.stream().filter(id -> !game.isPlayerDead(id)).count();
        boolean doctorAlive = !game.isPlayerDead(doctorId);
        boolean policeAlive = !game.isPlayerDead(policeId);

        long citizenTeamCount = citizenCount + (doctorAlive ? 1 : 0) + (policeAlive ? 1 : 0);

        if (mafiaCount >= citizenTeamCount) {
            // 마피아 팀 승리
            recordWin(mafiaIds);
            recordLoss(citizenIds);
            recordLossForLong(doctorId);
            recordLossForLong(policeId);
            endGame(game);
        } else if (mafiaCount == 0) {
            // 시민 팀 승리
            recordWin(citizenIds);
            recordWinForLong(doctorId);
            recordWinForLong(policeId);
            recordLoss(mafiaIds);
            endGame(game);
        }
    }

    @Override
    @Transactional
    public void handlePoliceTurn(Long gameId, Long suspectId) {
        Game game = findById(gameId);
        if (game.isPlayerDead(game.getPoliceId())) return;

        User suspect = userRepository.findById(suspectId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 경찰에게 조사받은 플레이어의 역할을 클라이언트로 전달하는 로직
        String role = game.getMafiaIds().contains(suspectId) ? "Mafia" :
                game.getDoctorId().equals(suspectId) ? "Doctor" :
                        game.getPoliceId().equals(suspectId) ? "Police" : "Citizen";

        // 클라이언트로 역할 전달
        String message = suspect.getNickname() + "의 역할은 " + role + "입니다.";
        messagingTemplate.convertAndSendToUser(
                game.getPoliceId().toString(),
                "/topic/game/" + gameId,
                message
        );
    }

    @Override
    @Transactional
    public void handleMafiaTurn(Long gameId, Long targetId) {
        Game game = findById(gameId);
        if (game.getMafiaIds().stream().allMatch(game::isPlayerDead)) return;

        game.addDeadThisNight(targetId);
        gameRepository.save(game);
    }

    @Override
    @Transactional
    public void handleDoctorTurn(Long gameId, Long targetId) {
        Game game = findById(gameId);
        if (game.isPlayerDead(game.getDoctorId())) return;

        if (game.getDeadThisNight().contains(targetId)) {
            game.getDeadThisNight().remove(targetId);
        }

        gameRepository.save(game);
    }

    @Override
    @Transactional
    public void announceNightResults(Long gameId) {
        Game game = findById(gameId);

        List<Long> deadThisNight = new ArrayList<>(game.getDeadThisNight());

        deadThisNight.forEach(playerId -> {
            User user = userRepository.findById(playerId).orElseThrow(() -> new RuntimeException("User not found"));
            game.addDeadPlayer(playerId);

            // 채팅 메시지로 공지
            String message = user.getNickname() + "님이 마피아에 의해 사망했습니다.";
            messagingTemplate.convertAndSend("/topic/game/" + gameId, message);
        });

        game.clearDeadThisNight();
        gameRepository.save(game);
        checkVictoryConditions(game);
    }

    @Override
    public Game findById(Long gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> new RuntimeException("게임을 찾을 수 없습니다."));
    }

    private void recordWin(List<Long> userIds) {
        userIds.forEach(userId -> {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
            user.plusWins();
            userRepository.save(user);
        });
    }

    private void recordWinForLong(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        user.plusWins();
        userRepository.save(user);
    }

    private void recordLoss(List<Long> userIds) {
        userIds.forEach(userId -> {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
            user.plusLosses();
            userRepository.save(user);
        });
    }

    private void recordLossForLong(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        user.plusLosses();
        userRepository.save(user);
    }

    private GameResponseDto convertToDto(Game game) {
        return new GameResponseDto(
                game.getId(),
                game.getRoom().getId(),
                game.getMafiaIds(),
                game.getCitizenIds(),
                game.getDoctorId(),
                game.getPoliceId(),
                game.getState().name(),
                game.getDeadPlayerIds()
        );
    }
}
