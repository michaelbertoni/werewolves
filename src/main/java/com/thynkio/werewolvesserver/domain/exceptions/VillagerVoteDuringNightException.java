package com.thynkio.werewolvesserver.domain.exceptions;

public class VillagerVoteDuringNightException extends GameException {
    public VillagerVoteDuringNightException(String id) {
        super(id);
    }

    @Override
    public String getMessage() {
        return "A villager cannot vote during the night.";
    }
}
