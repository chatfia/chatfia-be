package com.project.chatfiabe.domain.room.service.impl;

import com.project.chatfiabe.domain.room.dto.RoomRequestDto;
import com.project.chatfiabe.domain.room.dto.RoomResponseDto;
import com.project.chatfiabe.domain.room.dto.RoomUserResponseDto;
import com.project.chatfiabe.domain.room.entity.Room;
import com.project.chatfiabe.domain.room.repository.RoomRepository;
import com.project.chatfiabe.domain.room.service.RoomService;
import com.project.chatfiabe.domain.user.entity.User;
import com.project.chatfiabe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
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
                                .map(user -> new RoomUserResponseDto(user.getId(), user.getNickname()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    // 방 참여
    public RoomResponseDto joinRoom(Long roomId, User user, String password) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다"));
        if (room.isPrivate() && (password == null || !room.getPassword().equals(password))) {
            throw new RuntimeException("패스워드가 올바르지 않습니다.");
        }
        room.addPlayer(user);
        userRepository.save(user);
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

    // 유저 강퇴
    public void kickPlayer(Long roomId, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다"));
        if (!room.getHostId().equals(user.getId())) {
            throw new RuntimeException("방장만 강제퇴장 시킬 수 있습니다");
        }
        room.removePlayer(user);
        userRepository.save(user);
    }

    // 방에서 퇴장
    public void leaveRoom(Long roomId, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다"));
        room.removePlayer(user);
        userRepository.save(user);
        if (room.getNumberOfPlayer() == 0) {
            roomRepository.delete(room);
        }
    }

    // 방 이름으로 검색
    public List<RoomResponseDto> searchRoomsByName(String name) {
        return roomRepository.findByNameContaining(name).stream()
                .map(room -> new RoomResponseDto(
                        room.getId(),
                        room.getName(),
                        room.getHostId(),
                        room.isPrivate(),
                        room.getPlayers().stream()
                                .map(user -> new RoomUserResponseDto(user.getId(), user.getNickname()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    // 등록일 순으로 방 조회
    public List<RoomResponseDto> getRoomsSortedByCreatedDate(boolean ascending) {
        List<Room> rooms = ascending ? roomRepository.findAllByOrderByCreateAtAsc() : roomRepository.findAllByOrderByCreateAtDesc();
        return rooms.stream()
                .map(room -> new RoomResponseDto(
                        room.getId(),
                        room.getName(),
                        room.getHostId(),
                        room.isPrivate(),
                        room.getPlayers().stream()
                                .map(user -> new RoomUserResponseDto(user.getId(), user.getNickname()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    // 현재 참여 인원 순 방 조회
    public List<RoomResponseDto> getRoomsSortedByPlayerCount(boolean ascending) {
        List<Room> rooms = ascending ? roomRepository.findAllByOrderByPlayersSizeAsc() : roomRepository.findAllByOrderByPlayersSizeDesc();
        return rooms.stream()
                .map(room -> new RoomResponseDto(
                        room.getId(),
                        room.getName(),
                        room.getHostId(),
                        room.isPrivate(),
                        room.getPlayers().stream()
                                .map(user -> new RoomUserResponseDto(user.getId(), user.getNickname()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}
