package com.thynkio.werewolvesserver.service;

import com.thynkio.werewolvesserver.domain.exceptions.GameException;
import com.thynkio.werewolvesserver.domain.exceptions.PlayerNotFoundInGameException;
import com.thynkio.werewolvesserver.service.dto.WerewolvesGameDTO;

public interface WerewolvesGameService {

    String createGame();

    void joinGame(String playerName, String gameId) throws GameException;

    void leaveGame(String playerName, String gameId) throws GameException;

    void vote(String voterNickname, String votedNickname, String gameId) throws GameException;

    WerewolvesGameDTO getStatus(String gameId, String nickname) throws GameException;

    void startGame(String playerName, String gameId) throws GameException;
}
