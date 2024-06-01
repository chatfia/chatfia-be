package com.project.chatfiabe.domain.room.repository;

import com.project.chatfiabe.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
