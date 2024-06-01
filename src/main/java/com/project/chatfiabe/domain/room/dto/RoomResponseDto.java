package com.project.chatfiabe.domain.room.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RoomResponseDto {
    private Long id;
    private String name;
    private Long hostId;
    private boolean isPrivate;
    private List<UserInfoResponseDto> players;
}
