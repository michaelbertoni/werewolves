package com.thynkio.werewolvesserver.repository;

import com.thynkio.werewolvesserver.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerEntity {

    @ManyToOne
    private WerewolvesGameEntity game;

    @Id
    private String nickname;

    private Role role;

    private boolean isAlive;

    private int votedAgainst;
}
