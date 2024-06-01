package com.project.chatfiabe.domain.room.service;

import com.project.chatfiabe.domain.room.dto.RoomRequestDto;
import com.project.chatfiabe.domain.room.dto.RoomResponseDto;
import com.project.chatfiabe.domain.room.dto.RoomUserResponseDto;
import com.project.chatfiabe.domain.room.entity.Room;
import com.project.chatfiabe.domain.room.repository.RoomRepository;
import com.project.chatfiabe.domain.user.entity.User;
import com.project.chatfiabe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    // 방 생성
    public RoomResponseDto createRoom(RoomRequestDto requestDto, User user) {
        Room room = new Room(requestDto.getName(), user.getId(), requestDto.isPrivate(), requestDto.getPassword());
        room = roomRepository.save(room);
        return new RoomResponseDto(
                room.getId(),
                room.getName(),
                room.getHostId(),
                room.isPrivate(),
                room.getPlayers().stream()
                        .map(u -> new RoomUserResponseDto(u.getId(), u.getNickname()))
                        .collect(Collectors.toList())
        );
    }

    // 방 조회
    public List<RoomResponseDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(room -> new RoomResponseDto(
                        room.getId(),
                        room.getName(),
                        room.getHostId(),
                        room.isPrivate(),
                        room.getPlayers().stream()
                                .map(user -> new RoomUserResponseDto(user.getId(), user.getUsername()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    // 방 참여
    public RoomResponseDto joinRoom(Long roomId, User user, String password) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        if (room.isPrivate() && (password == null || !room.getPassword().equals(password))) {
            throw new RuntimeException("Invalid password");
        }
        room.addPlayer(user);
        userRepository.save(user);
        return new RoomResponseDto(
                room.getId(),
                room.getName(),
                room.getHostId(),
                room.isPrivate(),
                room.getPlayers().stream()
                        .map(u -> new RoomUserResponseDto(u.getId(), u.getUsername()))
                        .collect(Collectors.toList())
        );
    }

    // 유저 강퇴
    public void kickPlayer(Long roomId, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        if (!room.getHostId().equals(user.getId())) {
            throw new RuntimeException("Only the host can kick players");
        }
        room.removePlayer(user);
        userRepository.save(user);
    }

}
