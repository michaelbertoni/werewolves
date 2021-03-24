package com.thynkio.werewolvesserver.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)

public class DeadPlayerVoterException extends GameException {
    public DeadPlayerVoterException(String id) {
        super(id);
    }

    @Override
    public String getMessage() {
        return "You cannot vote when you are dead in the game.";
    }
}
