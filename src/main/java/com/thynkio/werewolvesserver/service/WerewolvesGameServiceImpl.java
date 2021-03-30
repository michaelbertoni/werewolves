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
    public void joinGame(String playerName, String gameId) throws GameException {
        WerewolvesGame game = getWerewolvesGame(gameId);
        game.addPlayer(playerName);
        saveGame(game);
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
     * Starts a game
     *
     * @param playerName
     * @param gameId
     */
    @Override
    public void startGame(String playerName, String gameId) throws GameException {
        WerewolvesGame game = getWerewolvesGame(gameId);
        Optional<Player> playerOptional = game.getPlayerFromNickname(playerName);
        if (playerOptional.isEmpty()) {
            throw new PlayerNotFoundInGameException(gameId);
        }
        game.start();
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
    public void vote(String voterNickname, String votedNickname, String gameId) throws GameException {
        WerewolvesGame game = getWerewolvesGame(gameId);
        game.vote(voterNickname, votedNickname);
        saveGame(game);
    }

    /**
     * Retrieve game status according to player's role
     *
     * @param gameId
     * @param nickname
     * @return game status as Object
     */
    @Override
    public WerewolvesGameDTO getStatus(String gameId, String nickname) throws GameException {
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
    }

    private WerewolvesGame getWerewolvesGame(String gameId) {
        Optional<WerewolvesGameEntity> gameEntity = this.werewolvesGameRepository.findById(gameId);
        if (gameEntity.isEmpty()) {
            throw new GameNotFoundException(gameId);
        }
        return modelMapper.map(gameEntity.get(), WerewolvesGame.class);
    }

    private void saveGame(WerewolvesGame game) {
        this.werewolvesGameRepository.save(modelMapper.map(game, WerewolvesGameEntity.class));
    }

}
