package com.thynkio.werewolvesserver.service;

import com.thynkio.werewolvesserver.service.dto.WerewolvesGameDTO;

public interface WerewolvesGameService {

    String createGame() throws Exception;

    void joinGame(String playerName, String gameId) throws Exception;

    void leaveGame(String playerName, String gameId) throws Exception;

    void vote(String voterNickname, String votedNickname, String gameId) throws Exception;

    WerewolvesGameDTO getStatus(String gameId, String nickname) throws Exception;

    void startGame(String playerName, String gameId) throws Exception;
}
