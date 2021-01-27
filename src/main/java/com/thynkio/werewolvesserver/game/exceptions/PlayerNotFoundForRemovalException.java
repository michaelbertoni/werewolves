package com.thynkio.werewolvesserver.game.exceptions;

public class PlayerNotFoundForRemovalException extends GameException {

    public PlayerNotFoundForRemovalException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "The player was not found in the game, it cannot be removed.";
    }
}
