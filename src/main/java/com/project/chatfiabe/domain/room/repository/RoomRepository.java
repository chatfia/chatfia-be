package com.project.chatfiabe.domain.room.repository;

import com.project.chatfiabe.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByNameContaining(String name);

    List<Room> findAllByOrderByCreateAtAsc();

    List<Room> findAllByOrderByCreateAtDesc();

    List<Room> findAllByOrderByPlayersSizeAsc();

    List<Room> findAllByOrderByPlayersSizeDesc();
}
