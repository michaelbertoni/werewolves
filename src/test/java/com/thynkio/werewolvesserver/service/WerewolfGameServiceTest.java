package com.thynkio.werewolvesserver.service;

import com.thynkio.werewolvesserver.config.ModelMapperConfiguration;
import com.thynkio.werewolvesserver.domain.Phase;
import com.thynkio.werewolvesserver.domain.Player;
import com.thynkio.werewolvesserver.domain.Role;
import com.thynkio.werewolvesserver.domain.WerewolvesGame;
import com.thynkio.werewolvesserver.domain.exceptions.GameException;
import com.thynkio.werewolvesserver.domain.exceptions.PlayerNotFoundInGameException;
import com.thynkio.werewolvesserver.domain.exceptions.PlayersWithIdenticalNicknameException;
import com.thynkio.werewolvesserver.repository.WerewolvesGameEntity;
import com.thynkio.werewolvesserver.repository.WerewolvesGameRepository;
import com.thynkio.werewolvesserver.service.dto.WerewolvesGameDTO;
import com.thynkio.werewolvesserver.service.exceptions.GameNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

    @Captor
    private ArgumentCaptor<WerewolvesGameEntity> werewolvesGameEntityArgumentCaptor;

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
        werewolvesGameService.joinGame("nickname", game.getId());

        // then
        Mockito.verify(werewolvesGameRepository).save(werewolvesGameEntityArgumentCaptor.capture());
        WerewolvesGame gameAfterVote = modelMapper.map(werewolvesGameEntityArgumentCaptor.getValue(), WerewolvesGame.class);
        assertFalse(gameAfterVote.isStarted());
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

    @Test
    public void whenJoinUnknownGame_exceptionIsThrown() {
        // given
        Mockito.when(werewolvesGameRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        // when

        // then
        assertThrows(GameNotFoundException.class, () -> werewolvesGameService.joinGame("nickname", "unknownGameId"));
    }

    @Test
    public void whenLeaveGame_NothingIsReturned() throws GameException {
        // given
        WerewolvesGame game = WerewolvesGame.createGame();
        game.addPlayer("nickname");
        WerewolvesGameEntity gameEntity = modelMapper.map(game, WerewolvesGameEntity.class);
        Mockito.when(werewolvesGameRepository.findById(Mockito.any())).thenReturn(Optional.of(gameEntity));

        // when

        // then
        assertDoesNotThrow(() -> werewolvesGameService.leaveGame("nickname", game.getId()));
    }

    @Test
    public void whenLeaveGameUnknownPlayer_GameExceptionIsThrown() throws GameException {
        // given
        WerewolvesGame game = WerewolvesGame.createGame();
        game.addPlayer("nickname");
        WerewolvesGameEntity gameEntity = modelMapper.map(game, WerewolvesGameEntity.class);
        Mockito.when(werewolvesGameRepository.findById(Mockito.any())).thenReturn(Optional.of(gameEntity));

        // when

        // then
        assertThrows(PlayerNotFoundInGameException.class, () -> werewolvesGameService.leaveGame("nickname2", game.getId()));
    }

    @Test
    public void whenPlayerStartsGame_gameIsStarted() throws GameException {
        // given
        WerewolvesGame game = createGameWith9Players();
        WerewolvesGameEntity gameEntity = modelMapper.map(game, WerewolvesGameEntity.class);

        // when
        Mockito.when(werewolvesGameRepository.findById(Mockito.any())).thenReturn(Optional.of(gameEntity));
        werewolvesGameService.startGame("nickname", game.getId());

        // then
        Mockito.verify(werewolvesGameRepository).save(werewolvesGameEntityArgumentCaptor.capture());
        WerewolvesGame gameAfterVote = modelMapper.map(werewolvesGameEntityArgumentCaptor.getValue(), WerewolvesGame.class);
        assertTrue(gameAfterVote.isStarted());
        assertEquals(Phase.NIGHT, gameAfterVote.getPhase());
    }

    @Test
    public void whenUnknownPlayerStartsGame_exceptionIsThrown() throws GameException {
        // given
        WerewolvesGame game = createGameWith9Players();
        WerewolvesGameEntity gameEntity = modelMapper.map(game, WerewolvesGameEntity.class);

        // when
        Mockito.when(werewolvesGameRepository.findById(Mockito.any())).thenReturn(Optional.of(gameEntity));

        // then
        assertThrows(PlayerNotFoundInGameException.class, () -> werewolvesGameService.startGame("unknown", game.getId()));
    }

    @Test
    public void whenPlayerVote_voteIsCountedAndPhaseReturned() throws GameException {
        // given
        WerewolvesGame game = createGameWith9Players();
        game.start();
        String firstWerewolf = game.getPlayers().stream().filter(Player::isWerewolf).findFirst().get().getNickname();
        String firstVillager = game.getPlayers().stream().filter(Player::isVillager).findFirst().get().getNickname();
        WerewolvesGameEntity gameEntity = modelMapper.map(game, WerewolvesGameEntity.class);

        // when
        Mockito.when(werewolvesGameRepository.findById(Mockito.any())).thenReturn(Optional.of(gameEntity));
        werewolvesGameService.vote(firstWerewolf, firstVillager, game.getId());

        // then
        Mockito.verify(werewolvesGameRepository).save(werewolvesGameEntityArgumentCaptor.capture());
        WerewolvesGame gameAfterVote = modelMapper.map(werewolvesGameEntityArgumentCaptor.getValue(), WerewolvesGame.class);
        assertEquals(1, gameAfterVote.getPlayerFromNickname(firstVillager).get().getVotedAgainst());
        assertEquals(Phase.NIGHT, gameAfterVote.getPhase());
    }

    @Test
    public void whenPlayerGetStatus_DTOisReturned() throws GameException {
        // given
        WerewolvesGame game = createGameWith9Players();
        game.start();
        WerewolvesGameEntity gameEntity = modelMapper.map(game, WerewolvesGameEntity.class);

        // when
        Mockito.when(werewolvesGameRepository.findById(Mockito.any())).thenReturn(Optional.of(gameEntity));

        // then
        WerewolvesGameDTO werewolvesGameDTO = werewolvesGameService.getStatus(game.getId(), "nickname");
        assertNotNull(werewolvesGameDTO);
        assertTrue(werewolvesGameDTO.isStarted());
        assertEquals(9, werewolvesGameDTO.getPlayers().size());
    }

    @Test
    public void whenUnknownPlayerGetStatus_PlayerUnknownExceptionIsThrown() throws GameException {
        // given
        WerewolvesGame game = createGameWith9Players();
        game.start();
        WerewolvesGameEntity gameEntity = modelMapper.map(game, WerewolvesGameEntity.class);

        // when
        Mockito.when(werewolvesGameRepository.findById(Mockito.any())).thenReturn(Optional.of(gameEntity));

        // then
        assertThrows(PlayerNotFoundInGameException.class, () -> werewolvesGameService.getStatus(game.getId(), "unknown"));
    }

    @Test
    public void whenVillagerPlayerGetStatus_AllPlayerInDTOAreVillagers() throws GameException {
        // given
        WerewolvesGame game = createGameWith9Players();
        game.start();
        String firstVillager = game.getPlayers().stream().filter(Player::isVillager).findFirst().get().getNickname();
        WerewolvesGameEntity gameEntity = modelMapper.map(game, WerewolvesGameEntity.class);

        // when
        Mockito.when(werewolvesGameRepository.findById(Mockito.any())).thenReturn(Optional.of(gameEntity));

        // then
        WerewolvesGameDTO werewolvesGameDTO = werewolvesGameService.getStatus(game.getId(), firstVillager);
        assertTrue(game.getPlayers()
                .stream()
                .anyMatch(player -> player.getRole().equals(Role.WEREWOLF)));
        assertTrue(werewolvesGameDTO.getPlayers()
                .stream()
                .noneMatch(playerDTO -> playerDTO.getRole().equals(Role.WEREWOLF)));
    }

    @Test
    public void whenVillagerPlayerGetStatusAtNightPhase_VillagerCannotSeeWerewolvesVotes() throws GameException {
        // given
        WerewolvesGame game = createGameWith9Players();
        game.start();
        String firstWerewolf = game.getPlayers().stream().filter(Player::isWerewolf).findFirst().get().getNickname();
        String firstVillager = game.getPlayers().stream().filter(Player::isVillager).findFirst().get().getNickname();
        game.vote(firstWerewolf, firstVillager);
        WerewolvesGameEntity gameEntity = modelMapper.map(game, WerewolvesGameEntity.class);

        // when
        Mockito.when(werewolvesGameRepository.findById(Mockito.any())).thenReturn(Optional.of(gameEntity));

        // then
        WerewolvesGameDTO werewolvesGameDTO = werewolvesGameService.getStatus(game.getId(), firstVillager);
        assertEquals(1, game.getPlayerFromNickname(firstVillager).get().getVotedAgainst());
        assertEquals(0,
                werewolvesGameDTO.getPlayers()
                        .stream()
                        .filter(playerDTO -> playerDTO.getNickname().equals(firstVillager))
                        .findFirst()
                        .get()
                        .getVotedAgainst()
        );
    }

    private WerewolvesGame createGameWith9Players() throws GameException {
        WerewolvesGame game = WerewolvesGame.createGame();
        game.addPlayer("nickname");
        game.addPlayer("nickname1");
        game.addPlayer("nickname2");
        game.addPlayer("nickname3");
        game.addPlayer("nickname4");
        game.addPlayer("nickname5");
        game.addPlayer("nickname6");
        game.addPlayer("nickname7");
        game.addPlayer("nickname8");
        return game;
    }
}