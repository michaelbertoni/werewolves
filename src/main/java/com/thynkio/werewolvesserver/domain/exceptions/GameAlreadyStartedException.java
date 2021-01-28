package com.thynkio.werewolvesserver.domain.exceptions;

public class GameAlreadyStartedException extends GameException {
    public GameAlreadyStartedException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "The game has already started, it cannot be restarted.";
    }
}
