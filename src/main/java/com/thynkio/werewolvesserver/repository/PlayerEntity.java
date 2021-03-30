package com.thynkio.werewolvesserver.repository;

import com.thynkio.werewolvesserver.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity(name = "player")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerEntity {
    @Id
    private String nickname;

    @ManyToOne
    @JoinColumn(name = "GAME_ID")
    private WerewolvesGameEntity game;

    private Role role;

    private boolean isAlive;

    private int votedAgainst;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerEntity that = (PlayerEntity) o;
        return isAlive == that.isAlive && votedAgainst == that.votedAgainst && nickname.equals(that.nickname)
                && role == that.role && game.getId().equals(that.getGame().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, role, isAlive, votedAgainst, game.getId());
    }
}
