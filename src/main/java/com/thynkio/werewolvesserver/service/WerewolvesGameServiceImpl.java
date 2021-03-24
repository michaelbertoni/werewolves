package com.thynkio.werewolvesserver.service;

import com.thynkio.werewolvesserver.domain.WerewolvesGame;
import com.thynkio.werewolvesserver.domain.exceptions.GameException;
import com.thynkio.werewolvesserver.repository.WerewolvesGameEntity;
import com.thynkio.werewolvesserver.repository.WerewolvesGameRepository;
import com.thynkio.werewolvesserver.service.exceptions.GameNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class WerewolvesGameServiceImpl implements WerewolvesGameService {

    private final ModelMapper modelMapper;
    private final WerewolvesGameRepository werewolvesGameRepository;

    public WerewolvesGameServiceImpl(ModelMapper modelMapper, WerewolvesGameRepository werewolvesGameRepository) {
        this.modelMapper = modelMapper;
        this.werewolvesGameRepository = werewolvesGameRepository;
    }

    /**
     * Creates a new game, persists it and returns new game id
     *
     * @return gameId
     */
    @Transactional
    public String createGame() {
        WerewolvesGame newGame = WerewolvesGame.createGame();
        saveGame(newGame);
        return newGame.getId();
    }

    /**
     * Adds a new player in a specific game
     *
     * @param playerName
     * @param gameId
     */
    @Transactional
    public boolean joinGame(String playerName, String gameId) throws GameException {
        WerewolvesGame game = getWerewolvesGame(gameId);
        game.addPlayer(playerName);
        saveGame(game);

        return game.isStarted();
    }

    /**
     * Removes a player in a specific game
     *
     * @param playerName
     * @param gameId
     */
    @Transactional
    public void leaveGame(String playerName, String gameId) throws GameException {
        WerewolvesGame game = getWerewolvesGame(gameId);
        game.removePlayer(playerName);
        saveGame(game);
    }

    /**
     * Saves a vote against a player
     *
     * @param voterNickname
     * @param votedNickname
     * @param gameId
     */
    @Transactional
    public String vote(String voterNickname, String votedNickname, String gameId) throws GameException {
        WerewolvesGame game = getWerewolvesGame(gameId);
        game.vote(voterNickname, votedNickname);
        saveGame(game);

        return game.getPhase().toString();
    }

    private WerewolvesGame getWerewolvesGame(String gameId) {
        Optional<WerewolvesGameEntity> gameEntity = this.werewolvesGameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new GameNotFoundException(gameId);
        }
        WerewolvesGame game = modelMapper.map(gameEntity.get(), WerewolvesGame.class);
        return game;
    }

    private void saveGame(WerewolvesGame game) {
        this.werewolvesGameRepository.save(modelMapper.map(game, WerewolvesGameEntity.class));
    }

}
