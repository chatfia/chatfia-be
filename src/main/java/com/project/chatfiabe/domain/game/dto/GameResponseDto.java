package com.project.chatfiabe.domain.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GameResponseDto {
    private Long id;
    private Long roomId;
    private List<Long> mafiaIds;
    private List<Long> citizenIds;
    private Long doctorId;
    private Long policeId;
    private String state;
    private List<Long> deadPlayerIds;

    public GameResponseDto(Long id, Long roomId, List<Long> mafiaIds, List<Long> citizenIds, Long doctorId, Long policeId, String state, List<Long> deadPlayerIds) {
        this.id = id;
        this.roomId = roomId;
        this.mafiaIds = mafiaIds;
        this.citizenIds = citizenIds;
        this.doctorId = doctorId;
        this.policeId = policeId;
        this.state = state;
        this.deadPlayerIds = deadPlayerIds;
    }
}
