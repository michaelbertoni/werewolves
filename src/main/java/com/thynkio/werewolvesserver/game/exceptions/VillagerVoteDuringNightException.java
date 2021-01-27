package com.thynkio.werewolvesserver.game.exceptions;

public class VillagerVoteDuringNightException extends GameException {
    public VillagerVoteDuringNightException(String id) {
        super(id);
    }

    @Override
    public String getMessage() {
        return "A villager cannot vote during the night.";
    }
}
