package com.thynkio.werewolvesserver.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class VoteBeforeGameStartsException extends GameException {
    public VoteBeforeGameStartsException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "You cannot vote if the game has not started.";
    }
}
