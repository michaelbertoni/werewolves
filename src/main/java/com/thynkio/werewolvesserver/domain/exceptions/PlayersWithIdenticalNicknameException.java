package com.thynkio.werewolvesserver.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PlayersWithIdenticalNicknameException extends GameException {
    public PlayersWithIdenticalNicknameException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "A player in this game already uses this nickname.";
    }
}
