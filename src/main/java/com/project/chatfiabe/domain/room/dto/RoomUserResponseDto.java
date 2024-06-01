package com.project.chatfiabe.domain.room.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoomUserResponseDto {
    private Long id;
    private String nickname;

    public RoomUserResponseDto(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
