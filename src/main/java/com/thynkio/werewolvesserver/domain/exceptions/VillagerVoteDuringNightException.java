package com.thynkio.werewolvesserver.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class VillagerVoteDuringNightException extends GameException {
    public VillagerVoteDuringNightException(String id) {
        super(id);
    }

    @Override
    public String getMessage() {
        return "A villager cannot vote during the night.";
    }
}
