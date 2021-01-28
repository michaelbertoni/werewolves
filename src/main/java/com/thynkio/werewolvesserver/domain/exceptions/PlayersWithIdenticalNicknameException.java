package com.thynkio.werewolvesserver.domain.exceptions;

public class PlayersWithIdenticalNicknameException extends GameException {
    public PlayersWithIdenticalNicknameException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "A player in this game already uses this nickname.";
    }
}
