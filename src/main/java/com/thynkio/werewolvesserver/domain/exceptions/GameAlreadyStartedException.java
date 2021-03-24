package com.thynkio.werewolvesserver.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class GameAlreadyStartedException extends GameException {
    public GameAlreadyStartedException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "The game has already started, it cannot be restarted.";
    }
}
