package com.thynkio.werewolvesserver.service;

import com.thynkio.werewolvesserver.domain.exceptions.GameException;

public interface WerewolvesGameService {

    String createGame();

    boolean joinGame(String playerName, String gameId) throws GameException;

    void leaveGame(String playerName, String gameId) throws GameException;

    String vote(String voterNickname, String votedNickname, String gameId) throws GameException;
}
