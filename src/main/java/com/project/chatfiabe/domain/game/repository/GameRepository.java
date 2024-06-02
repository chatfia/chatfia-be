package com.project.chatfiabe.domain.game.repository;

import com.project.chatfiabe.domain.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByRoomId(Long id);
}
