package com.thynkio.werewolvesserver.game;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class Player {
    private String nickname;
    private final String id;
    private Role role;

    public Player(String nickname) {
        this.id = UUID.randomUUID().toString();
        this.nickname = nickname;
        this.role = Role.VILLAGER;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
