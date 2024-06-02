package com.project.chatfiabe.domain.room.controller;

import com.project.chatfiabe.domain.room.dto.RoomRequestDto;
import com.project.chatfiabe.domain.room.dto.RoomResponseDto;
import com.project.chatfiabe.domain.room.service.RoomService;
import com.project.chatfiabe.domain.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;

    // 방 생성
    @PostMapping
    public ResponseEntity<RoomResponseDto> createRoom(@RequestBody RoomRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        RoomResponseDto createdRoom = roomService.createRoom(requestDto, userDetails.getUser());
        return new ResponseEntity<>(createdRoom, HttpStatus.CREATED);
    }

    // 모든 방 조회
    @GetMapping
    public ResponseEntity<List<RoomResponseDto>> getAllRooms() {
        List<RoomResponseDto> allRooms = roomService.getAllRooms();
        return new ResponseEntity<>(allRooms, HttpStatus.OK);
    }

    // 방 참여
    @PostMapping("/{roomId}/join")
    public ResponseEntity<RoomResponseDto> joinRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @RequestParam(required = false) String password) {
        RoomResponseDto joinedRoom = roomService.joinRoom(roomId, userDetails.getUser(), password);
        return new ResponseEntity<>(joinedRoom, HttpStatus.OK);
    }

    // 유저 강퇴
    @PostMapping("/{roomId}/kick")
    public ResponseEntity<Void> kickPlayer(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        roomService.kickPlayer(roomId, userDetails.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 방에서 유저 퇴장
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<Void> leaveRoom(@PathVariable Long roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        roomService.leaveRoom(roomId, userDetails.getUser());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
