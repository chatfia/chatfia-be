package com.project.chatfiabe.domain.game.service;

import com.project.chatfiabe.domain.game.dto.GameResponseDto;
import com.project.chatfiabe.domain.game.entity.Game;

public interface GameService {
    GameResponseDto startGame(Long roomId);
    void assignRoles(Game game);
    GameResponseDto proceedToNextPhase(Long gameId);
    void endGame(Game game);
    void checkVictoryConditions(Game game);
    void handlePoliceTurn(Long gameId, Long suspectId);
    void handleMafiaTurn(Long gameId, Long targetId);
    void handleDoctorTurn(Long gameId, Long targetId);
    void announceNightResults(Long gameId);
    Game findById(Long gameId);

}
