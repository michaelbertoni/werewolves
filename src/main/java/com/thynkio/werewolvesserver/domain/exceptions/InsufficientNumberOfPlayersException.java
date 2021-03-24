package com.thynkio.werewolvesserver.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InsufficientNumberOfPlayersException extends GameException {
    public InsufficientNumberOfPlayersException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "The game needs at least 4 players to start.";
    }
}
