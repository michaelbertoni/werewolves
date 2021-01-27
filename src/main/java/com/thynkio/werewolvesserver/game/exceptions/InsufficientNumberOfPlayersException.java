package com.thynkio.werewolvesserver.game.exceptions;

public class InsufficientNumberOfPlayersException extends GameException {
    public InsufficientNumberOfPlayersException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "The game needs at least 4 players to start.";
    }
}
