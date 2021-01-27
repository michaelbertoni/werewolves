package com.thynkio.werewolvesserver.game.exceptions;

public class DeadPlayerVotedException extends GameException {
    public DeadPlayerVotedException(String id) {
        super(id);
    }

    @Override
    public String getMessage() {
        return "You cannot vote for someone who is dead in the game.";
    }
}
