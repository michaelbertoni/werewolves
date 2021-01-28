package com.thynkio.werewolvesserver.domain.exceptions;

public class WerewolfVoteForOtherWerewolfException extends GameException{
    public WerewolfVoteForOtherWerewolfException(String gameId) {
        super(gameId);
    }

    @Override
    public String getMessage() {
        return "A werewolf cannot vote for another werewolf during the night.";
    }
}
