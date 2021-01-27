package com.thynkio.werewolvesserver.game;

import com.thynkio.werewolvesserver.game.exceptions.GameAlreadyStartedException;
import com.thynkio.werewolvesserver.game.exceptions.GameException;
import com.thynkio.werewolvesserver.game.exceptions.InsufficientNumberOfPlayersException;
import com.thynkio.werewolvesserver.game.exceptions.PlayersWithIdenticalNicknameException;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class WerewolvesGame {
    private final String id;
    private final Map<String, Player> players;
    private boolean started;

    public WerewolvesGame() {
        this.id = UUID.randomUUID().toString();
        this.players = new HashMap<>();
    }

    public void start() throws GameException {
        if (players.size() < 3) {
            throw new InsufficientNumberOfPlayersException(id);
        }
        
        if (started) {
            throw new GameAlreadyStartedException(id);
        }

        assignRoleToPlayers();
        
        started = true;
    }

    private void assignRoleToPlayers() {
        List<String> playerNicknames = new ArrayList<>(players.keySet());
        int numberOfWerewolves = players.size() / 6 + 1;
        while (getWerewolvesCountInGame() < numberOfWerewolves) {
            String randomPlayerNickname = playerNicknames.get(ThreadLocalRandom.current().nextInt(0, players.size()));
            players.get(randomPlayerNickname).setRole(Role.WEREWOLF);
        }
    }

    public int getWerewolvesCountInGame() {
        return (int) getPlayers()
                .values()
                .stream()
                .filter(player -> player.getRole().equals(Role.WEREWOLF))
                .count();
    }

    public void addPlayer(Player player) throws GameException {
        if (players.containsKey(player.getNickname())) {
            throw new PlayersWithIdenticalNicknameException(id);
        }
        players.put(player.getNickname(), player);
    }
}
