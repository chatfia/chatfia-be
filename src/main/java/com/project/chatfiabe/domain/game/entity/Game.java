package com.project.chatfiabe.domain.game.entity;

import com.project.chatfiabe.domain.room.entity.Room;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Room room;

    @ElementCollection
    private List<Long> mafiaIds;

    @ElementCollection
    private List<Long> citizenIds;

    private Long doctorId;

    private Long policeId;

    @Enumerated(EnumType.STRING)
    private GameState state;

    @ElementCollection
    private List<Long> deadPlayerIds;

    @ElementCollection
    private List<Long> deadThisNight;

    public enum GameState {
        DAY, NIGHT, ENDED
    }

    @Builder
    public Game(Room room, List<Long> mafiaIds, List<Long> citizenIds, Long doctorId, Long policeId) {
        this.room = room;
        this.mafiaIds = mafiaIds;
        this.citizenIds = citizenIds;
        this.doctorId = doctorId;
        this.policeId = policeId;
        this.state = GameState.DAY;
        this.deadPlayerIds = new ArrayList<>();
        this.deadThisNight = new ArrayList<>();
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void setDoctorId(Long id) {
        this.doctorId = id;
    }

    public void setPoliceId(Long id) {
        this.policeId = id;
    }

    public Game(Room room) {
        this.room = room;
    }

    public boolean isPlayerDead(Long playerId) {
        return deadPlayerIds.contains(playerId);
    }

    public void addDeadPlayer(Long playerId) {
        deadPlayerIds.add(playerId);
    }

    public void addDeadThisNight(Long playerId) {
        deadThisNight.add(playerId);
    }

    public void clearDeadThisNight() {
        deadThisNight.clear();
    }

}
