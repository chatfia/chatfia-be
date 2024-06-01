package com.project.chatfiabe.domain.room.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class RoomRequestDto {
    private String name;
    private boolean isPrivate;
    private String password;
}
