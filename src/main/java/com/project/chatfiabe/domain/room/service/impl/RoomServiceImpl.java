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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;


    /**
     * 방 생성
     * @param requestDto 방 생성 요청 DTO
     * @param user       방을 생성하는 사용자
     * @return 생성된 방 정보 DTO
     */
    @Override
    @Transactional
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

    /**
     * 모든 방 조회
     * @return 모든 방 정보 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
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

    /**
     * 방 참여
     * @param roomId   참여할 방 ID
     * @param user     참여하는 사용자
     * @param password 방 비밀번호 (비공개 방일 경우 필요)
     * @return 참여한 방 정보 DTO
     */
    @Override
    @Transactional
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

    /**
     * 유저 강퇴
     * @param roomId 강퇴할 유저가 있는 방 ID
     * @param user   강퇴할 유저
     */
    @Override
    @Transactional
    public void kickPlayer(Long roomId, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다"));
        if (!room.getHostId().equals(user.getId())) {
            throw new RuntimeException("방장만 강제퇴장 시킬 수 있습니다");
        }
        room.removePlayer(user);
        userRepository.save(user);
    }

    /**
     * 방에서 퇴장
     * @param roomId 퇴장할 방 ID
     * @param user   방에서 퇴장할 유저
     */
    @Override
    @Transactional
    public void leaveRoom(Long roomId, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("방을 찾을 수 없습니다"));
        room.removePlayer(user);
        userRepository.save(user);
        if (room.getNumberOfPlayer() == 0) {
            roomRepository.delete(room);
        } else {
            // 방장이 나갈 경우, 다음 플레이어에게 방장 권한을 위임
            if (room.getHostId().equals(user.getId())) {
                room.setHostId(room.getPlayers().get(0).getId());
            }
            roomRepository.save(room);
        }
    }

    /**
     * 방 이름으로 검색
     * @param name 검색할 방 이름
     * @return 이름에 해당하는 방 정보 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
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

    /**
     * 등록일 순으로 방 조회
     * @param ascending 오름차순 정렬 여부
     * @return 정렬된 방 정보 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
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

    /**
     * 현재 참여 인원 순 방 조회
     * @param ascending 오름차순 정렬 여부
     * @return 정렬된 방 정보 DTO 리스트
     */
    @Override
    @Transactional(readOnly = true)
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
