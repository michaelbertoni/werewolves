package com.thynkio.werewolvesserver.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class VoteFromUnknownPlayerException extends GameException {
    public VoteFromUnknownPlayerException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "Unknown player from this game cannot vote.";
    }
}
