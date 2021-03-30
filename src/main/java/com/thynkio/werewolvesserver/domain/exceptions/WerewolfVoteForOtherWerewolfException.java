package com.thynkio.werewolvesserver.domain.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class WerewolfVoteForOtherWerewolfException extends GameException {
    public WerewolfVoteForOtherWerewolfException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "A werewolf cannot vote for another werewolf during the night.";
    }
}
