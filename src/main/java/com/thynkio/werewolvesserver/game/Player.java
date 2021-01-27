package com.thynkio.werewolvesserver.game;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class Player {
    private String nickname;
    private final String id;
    private Role role;
    private boolean alive;

    public Player(String nickname) {
        this.id = UUID.randomUUID().toString();
        this.nickname = nickname;
        this.role = Role.VILLAGER;
        this.alive = true;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void kill() {
        alive = false;
    }

    public boolean isWerewolf() {
        return Role.WEREWOLF.equals(role);
    }

    public boolean isVillager() {
        return Role.VILLAGER.equals(role);
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(nickname, player.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }
}
