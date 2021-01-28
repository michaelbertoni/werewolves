package com.thynkio.werewolvesserver.domain;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Player implements Comparable<Player> {
    private final String nickname;
    private Role role;
    private boolean alive;
    private int votedAgainst;

    public Player(String nickname) {
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

    public void addVote() {
        votedAgainst = ++votedAgainst;
    }

    public void resetVote() {
        votedAgainst = 0;
    }

    public void resurrect() {
        alive = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return nickname.equals(player.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname);
    }

    @Override
    public int compareTo(Player otherPlayer) {
        return otherPlayer.getVotedAgainst() - votedAgainst;
    }
}
