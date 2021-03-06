package com.thynkio.werewolvesserver.domain.exceptions;

public class GameException extends RuntimeException {

    private final String gameId;

    public GameException(String gameId) {
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
}
