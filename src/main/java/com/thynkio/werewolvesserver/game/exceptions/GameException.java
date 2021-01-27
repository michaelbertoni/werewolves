package com.thynkio.werewolvesserver.game.exceptions;

public class GameException extends Throwable {

    private String gameId;

    public GameException(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
}
