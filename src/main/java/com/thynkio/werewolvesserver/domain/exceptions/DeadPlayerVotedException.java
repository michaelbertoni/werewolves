package com.thynkio.werewolvesserver.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DeadPlayerVotedException extends GameException {
    public DeadPlayerVotedException(String id) {
        super(id);
    }

    @Override
    public String getMessage() {
        return "You cannot vote for someone who is dead in the game.";
    }
}
