package com.thynkio.werewolvesserver.domain.exceptions;

public class VoteForUnknownPlayerException extends GameException {
    public VoteForUnknownPlayerException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "You cannot vote for this player, it does not exist.";
    }
}
