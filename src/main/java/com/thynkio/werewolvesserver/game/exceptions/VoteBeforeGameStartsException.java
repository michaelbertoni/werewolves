package com.thynkio.werewolvesserver.game.exceptions;

public class VoteBeforeGameStartsException extends GameException {
    public VoteBeforeGameStartsException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "You cannot vote if the game has not started.";
    }
}
