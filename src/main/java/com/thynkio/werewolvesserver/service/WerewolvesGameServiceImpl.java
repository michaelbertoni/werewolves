package com.thynkio.werewolvesserver.service;

import com.thynkio.werewolvesserver.domain.WerewolvesGame;
import com.thynkio.werewolvesserver.repository.PlayerRepository;
import com.thynkio.werewolvesserver.repository.WerewolvesGameEntity;
import com.thynkio.werewolvesserver.repository.WerewolvesGameRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class WerewolvesGameServiceImpl implements WerewolvesGameService {

    private final ModelMapper modelMapper;
    private final WerewolvesGameRepository werewolvesGameRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public WerewolvesGameServiceImpl(ModelMapper modelMapper, WerewolvesGameRepository werewolvesGameRepository, PlayerRepository playerRepository) {
        this.modelMapper = modelMapper;
        this.werewolvesGameRepository = werewolvesGameRepository;
        this.playerRepository = playerRepository;
    }

    /**
     * Creates a new game, persists it and returns new game id
     *
     * @return gameId
     */
    @Transactional
    public String createGame() {
        WerewolvesGame newGame = new WerewolvesGame();
        this.werewolvesGameRepository.save(modelMapper.map(newGame, WerewolvesGameEntity.class));
        return newGame.getId();
    }
}
