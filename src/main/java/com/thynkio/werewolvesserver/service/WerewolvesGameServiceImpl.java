package com.thynkio.werewolvesserver.service;

import com.thynkio.werewolvesserver.domain.Phase;
import com.thynkio.werewolvesserver.domain.Player;
import com.thynkio.werewolvesserver.domain.Role;
import com.thynkio.werewolvesserver.domain.WerewolvesGame;
import com.thynkio.werewolvesserver.domain.exceptions.GameException;
import com.thynkio.werewolvesserver.domain.exceptions.PlayerNotFoundInGameException;
import com.thynkio.werewolvesserver.repository.WerewolvesGameEntity;
import com.thynkio.werewolvesserver.repository.WerewolvesGameRepository;
import com.thynkio.werewolvesserver.service.dto.WerewolvesGameDTO;
import com.thynkio.werewolvesserver.service.exceptions.GameNotFoundException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class WerewolvesGameServiceImpl implements WerewolvesGameService {

    private static final Logger logger = LoggerFactory.getLogger(WerewolvesGameServiceImpl.class);

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
    public String createGame() throws Exception {
        try {
            logger.info("Creating new game");
            WerewolvesGame newGame = WerewolvesGame.createGame();
            saveGame(newGame);
            return newGame.getId();
        } catch (Exception e) {
            return handleExceptions(e);
        }
    }

    /**
     * Adds a new player in a specific game
     *
     * @param playerName
     * @param gameId
     */
    @Transactional
    public void joinGame(String playerName, String gameId) throws Exception {
        try {
            logger.info(String.format("Player %s joins game %s", playerName, gameId));
            WerewolvesGame game = getWerewolvesGame(gameId);
            game.addPlayer(playerName);
            saveGame(game);
        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    /**
     * Removes a player in a specific game
     *
     * @param playerName
     * @param gameId
     */
    @Transactional
    public void leaveGame(String playerName, String gameId) throws Exception {
        try {
            logger.info(String.format("Player %s leaves game %s", playerName, gameId));
            WerewolvesGame game = getWerewolvesGame(gameId);
            game.removePlayer(playerName);
            saveGame(game);
        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    /**
     * Starts a game
     *
     * @param playerName
     * @param gameId
     */
    @Override
    public void startGame(String playerName, String gameId) throws Exception {
        try {
            logger.info(String.format("Player %s starts game %s", playerName, gameId));
            WerewolvesGame game = getWerewolvesGame(gameId);
            Optional<Player> playerOptional = game.getPlayerFromNickname(playerName);
            if (playerOptional.isEmpty()) {
                throw new PlayerNotFoundInGameException(gameId);
            }
            game.start();
            saveGame(game);
        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    /**
     * Saves a vote against a player
     *
     * @param voterNickname
     * @param votedNickname
     * @param gameId
     */
    @Transactional
    public void vote(String voterNickname, String votedNickname, String gameId) throws Exception {
        try {
            logger.info(String.format("Player %s votes against %s in game %s", voterNickname, votedNickname, gameId));
            WerewolvesGame game = getWerewolvesGame(gameId);
            game.vote(voterNickname, votedNickname);
            saveGame(game);
        } catch (Exception e) {
            handleExceptions(e);
        }
    }

    /**
     * Retrieve game status according to player's role
     *
     * @param gameId
     * @param nickname
     * @return game status as Object
     */
    @Override
    public WerewolvesGameDTO getStatus(String gameId, String nickname) throws Exception {
        try {
            logger.info(String.format("Player %s retrieves status of game %s", nickname, gameId));
            WerewolvesGame game = getWerewolvesGame(gameId);
            if (game.getPlayerFromNickname(nickname).isEmpty()) {
                throw new PlayerNotFoundInGameException(gameId);
            }
            WerewolvesGameDTO gameDTO = modelMapper.map(game, WerewolvesGameDTO.class);
            // if player role is not werewolf, show all players as villagers
            if (game.getPlayerFromNickname(nickname).get().isVillager()) {
                gameDTO.getPlayers().forEach(playerDTO -> playerDTO.setRole(Role.VILLAGER));
                // if phase is night, don't show werewolves votes
                if (game.getPhase().equals(Phase.NIGHT)) {
                    gameDTO.getPlayers().forEach(playerDTO -> playerDTO.setVotedAgainst(0));
                }
            }
            return gameDTO;
        } catch (Exception e) {
            handleExceptions(e);
        }
        return null;
    }

    private WerewolvesGame getWerewolvesGame(String gameId) {
        Optional<WerewolvesGameEntity> gameEntity = this.werewolvesGameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new GameNotFoundException(gameId);
        }
        return modelMapper.map(gameEntity.get(), WerewolvesGame.class);
    }

    private void saveGame(WerewolvesGame game) {
        WerewolvesGameEntity updatedGameEntity = modelMapper.map(game, WerewolvesGameEntity.class);
        updatedGameEntity.getPlayers().forEach(playerEntity -> playerEntity.setGame(updatedGameEntity));
        this.werewolvesGameRepository.save(updatedGameEntity);
        logger.info(String.format("Werewolf game %s modifications persisted", game.getId()));
    }

    private String handleExceptions(Exception e) throws Exception {
        if (e instanceof GameException) {
            logger.info(e.getLocalizedMessage(), e);
        } else {
            logger.error("An unknown exception was thrown", e);
        }
        throw e;
    }

}
