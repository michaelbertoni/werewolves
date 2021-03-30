package com.thynkio.werewolvesserver.service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GameNotFoundException extends RuntimeException {
    private final String gameId;

    public GameNotFoundException(String gameId) {
        super(String.format("Game with id %s was not found", gameId));
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }
}
