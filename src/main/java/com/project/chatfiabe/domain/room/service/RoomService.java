package com.project.chatfiabe.domain.room.service;

import com.project.chatfiabe.domain.room.dto.RoomRequestDto;
import com.project.chatfiabe.domain.room.dto.RoomResponseDto;
import com.project.chatfiabe.domain.user.entity.User;

import java.util.List;

public interface RoomService {
    RoomResponseDto createRoom(RoomRequestDto requestDto, User user);

    List<RoomResponseDto> getAllRooms();

    RoomResponseDto joinRoom(Long roomId, User user, String password);

    void kickPlayer(Long roomId, User user);

    void leaveRoom(Long roomId, User user);

    List<RoomResponseDto> searchRoomsByName(String name);

    List<RoomResponseDto> getRoomsSortedByCreatedDate(boolean ascending);

    List<RoomResponseDto> getRoomsSortedByPlayerCount(boolean ascending);


}
