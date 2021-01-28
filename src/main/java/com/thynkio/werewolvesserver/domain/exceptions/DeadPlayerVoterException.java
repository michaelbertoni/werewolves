package com.thynkio.werewolvesserver.domain.exceptions;

public class DeadPlayerVoterException extends GameException {
    public DeadPlayerVoterException(String id) {
        super(id);
    }

    @Override
    public String getMessage() {
        return "You cannot vote when you are dead in the game.";
    }
}
