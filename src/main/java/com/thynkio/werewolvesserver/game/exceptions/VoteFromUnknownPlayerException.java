package com.thynkio.werewolvesserver.game.exceptions;

public class VoteFromUnknownPlayerException extends GameException {
    public VoteFromUnknownPlayerException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "Unknown player from this game cannot vote.";
    }
}
