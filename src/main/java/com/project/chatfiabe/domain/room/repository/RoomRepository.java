package com.project.chatfiabe.domain.room.repository;

import com.project.chatfiabe.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByNameContaining(String name);

    List<Room> findAllByOrderByCreateAtAsc();

    List<Room> findAllByOrderByCreateAtDesc();

    @Query("SELECT r FROM Room r LEFT JOIN r.players p GROUP BY r ORDER BY COUNT(p)")
    List<Room> findAllByOrderByPlayersSizeAsc();

    @Query("SELECT r FROM Room r LEFT JOIN r.players p GROUP BY r ORDER BY COUNT(p) DESC")
    List<Room> findAllByOrderByPlayersSizeDesc();
}
