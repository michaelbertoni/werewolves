package com.thynkio.werewolvesserver.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class PlayerNotFoundInGameException extends GameException {

    public PlayerNotFoundInGameException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "The player was not found in the game.";
    }
}
