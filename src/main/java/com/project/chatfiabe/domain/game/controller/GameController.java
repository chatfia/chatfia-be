package com.project.chatfiabe.domain.game.controller;

import com.project.chatfiabe.domain.game.dto.GameResponseDto;
import com.project.chatfiabe.domain.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @PostMapping("/start/{roomId}")
    public ResponseEntity<GameResponseDto> startGame(@PathVariable Long roomId) {
        GameResponseDto game = gameService.startGame(roomId);
        return new ResponseEntity<>(game, HttpStatus.CREATED);
    }

    @PostMapping("/{gameId}/next-phase")
    public ResponseEntity<GameResponseDto> proceedToNextPhase(@PathVariable Long gameId) {
        GameResponseDto game = gameService.proceedToNextPhase(gameId);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @PostMapping("/{gameId}/police-turn")
    public ResponseEntity<Void> handlePoliceTurn(@PathVariable Long gameId, @RequestParam Long suspectId) {
        gameService.handlePoliceTurn(gameId, suspectId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{gameId}/mafia-turn")
    public ResponseEntity<Void> handleMafiaTurn(@PathVariable Long gameId, @RequestParam Long targetId) {
        gameService.handleMafiaTurn(gameId, targetId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{gameId}/doctor-turn")
    public ResponseEntity<Void> handleDoctorTurn(@PathVariable Long gameId, @RequestParam Long targetId) {
        gameService.handleDoctorTurn(gameId, targetId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
