package com.thynkio.werewolvesserver.repository;

import com.thynkio.werewolvesserver.domain.Role;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PlayerEntity {

    @ManyToOne
    @JoinColumn(name="game_id")
    private WerewolvesGameEntity game;

    @Id
    private String nickname;

    private Role role;

    private boolean isAlive;

    private int votedAgainst;
}
