package com.thynkio.werewolvesserver.service;

import com.thynkio.werewolvesserver.config.ModelMapperConfiguration;
import com.thynkio.werewolvesserver.domain.WerewolvesGame;
import com.thynkio.werewolvesserver.domain.exceptions.GameException;
import com.thynkio.werewolvesserver.domain.exceptions.PlayersWithIdenticalNicknameException;
import com.thynkio.werewolvesserver.repository.WerewolvesGameEntity;
import com.thynkio.werewolvesserver.repository.WerewolvesGameRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WerewolvesGameServiceImpl.class)
@ContextConfiguration(classes = ModelMapperConfiguration.class)
class WerewolfGameServiceTest {

    @Autowired
    private ModelMapper modelMapper;

    @MockBean
    private WerewolvesGameRepository werewolvesGameRepository;

    @Autowired
    private WerewolvesGameService werewolvesGameService;

    @Test
    public void whenCreateGame_gameIdIsReturned() {
        // given

        // when
        String newGameId = werewolvesGameService.createGame();

        // then
        assertNotNull(newGameId);
        assertNotEquals("", newGameId);
    }

    @Test
    public void whenJoinGame_gameStatusIsReturned() throws GameException {
        // given
        WerewolvesGame game = WerewolvesGame.createGame();
        WerewolvesGameEntity gameEntity = modelMapper.map(game, WerewolvesGameEntity.class);
        Mockito.when(werewolvesGameRepository.findById(Mockito.any())).thenReturn(Optional.of(gameEntity));

        // when
        Boolean isStarted = werewolvesGameService.joinGame("nickname", game.getId());

        // then
        assertNotNull(isStarted);
        assertFalse(isStarted);
    }

    @Test
    public void whenJoinGameWithExistingNickname_exceptionIsThrown() throws GameException {
        // given
        WerewolvesGame game = WerewolvesGame.createGame();
        game.addPlayer("nickname");
        WerewolvesGameEntity gameEntity = modelMapper.map(game, WerewolvesGameEntity.class);
        Mockito.when(werewolvesGameRepository.findById(Mockito.any())).thenReturn(Optional.of(gameEntity));

        // when

        // then
        assertThrows(PlayersWithIdenticalNicknameException.class, () -> werewolvesGameService.joinGame("nickname", game.getId()));
    }
}