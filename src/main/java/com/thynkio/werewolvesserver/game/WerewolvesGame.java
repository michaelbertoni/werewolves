package com.thynkio.werewolvesserver.game;

import com.thynkio.werewolvesserver.game.exceptions.InsufficientNumberOfPlayersException;
import com.thynkio.werewolvesserver.game.exceptions.PlayersWithIdenticalNicknameException;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class WerewolvesGame {
    private String id;
    private Set<Player> players;

    public WerewolvesGame() {
        this.id = UUID.randomUUID().toString();
        this.players = new HashSet<>();
    }

    public void start() throws InsufficientNumberOfPlayersException {
        if (this.players.size() < 3) {
            throw new InsufficientNumberOfPlayersException();
        }
    }

    public void addPlayer(Player player) throws PlayersWithIdenticalNicknameException {
        if (this.players.stream().anyMatch(playerInGame -> playerInGame.getNickname().equals(player.getNickname()))) {
            throw new PlayersWithIdenticalNicknameException();
        }
        this.players.add(player);
    }
}
