package com.thynkio.werewolvesserver.rest;

import com.thynkio.werewolvesserver.domain.Player;
import com.thynkio.werewolvesserver.domain.WerewolvesGame;
import com.thynkio.werewolvesserver.domain.exceptions.GameException;
import com.thynkio.werewolvesserver.service.WerewolvesGameService;
import com.thynkio.werewolvesserver.service.dto.PlayerDTO;
import com.thynkio.werewolvesserver.service.dto.WerewolvesGameDTO;
import com.thynkio.werewolvesserver.service.exceptions.GameNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(WerewolvesGameController.class)
class WerewolfGameControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WerewolvesGameService werewolvesGameService;

    @Test
    public void givenPostGame_returnsString() throws Exception {
        // given
        WerewolvesGame werewolvesGame = WerewolvesGame.createGame();

        // when
        when(werewolvesGameService.createGame()).thenReturn(werewolvesGame.getId());
        MvcResult result = mvc.perform(post("/game")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        // then
        assertEquals(werewolvesGame.getId(), result.getResponse().getContentAsString());
    }

    @Test
    public void givenJoinGame_returns200() throws Exception {
        // given
        WerewolvesGame werewolvesGame = WerewolvesGame.createGame();

        // when
        doAnswer(invocationOnMock -> {
            String nickname = invocationOnMock.getArgument(0);
            werewolvesGame.addPlayer(nickname);
            return null;
        }).when(werewolvesGameService).joinGame(any(String.class), any(String.class));

        mvc.perform(post("/join")
                .param("playerName", "nickname")
                .param("gameId", werewolvesGame.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        assertTrue(werewolvesGame.getPlayerFromNickname("nickname").isPresent());
    }

    @Test
    public void givenJoinGameWithExistingNickname_return400() throws Exception {
        // given
        WerewolvesGame werewolvesGame = WerewolvesGame.createGame();
        werewolvesGame.addPlayer("nickname");

        // when
        doAnswer(invocationOnMock -> {
            String nickname = invocationOnMock.getArgument(0);
            werewolvesGame.addPlayer(nickname);
            return null;
        }).when(werewolvesGameService).joinGame(any(String.class), any(String.class));

        // then
        mvc.perform(post("/join")
                .param("playerName", "nickname")
                .param("gameId", werewolvesGame.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"message\":\"Game error\",\"details\":[\"A player in this game already uses this nickname.\"]}"));
        assertEquals(1, werewolvesGame.getPlayers().size());
    }

    @Test
    public void givenJoinUnknownGame_returns404() throws Exception {
        // given


        // when
        doAnswer(invocationOnMock -> {
            String gameId = invocationOnMock.getArgument(1);
            throw new GameNotFoundException(gameId);
        }).when(werewolvesGameService).joinGame(any(String.class), any(String.class));

        // then
        mvc.perform(post("/join")
                .param("playerName", "nickname")
                .param("gameId", "unknownGameId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Game not found\",\"details\":[\"Game with id unknownGameId was not found\"]}"));
    }

    @Test
    public void givenLeaveGame_returns200() throws Exception {
        // given
        WerewolvesGame werewolvesGame = WerewolvesGame.createGame();
        werewolvesGame.addPlayer("nickname");

        // when
        doAnswer(invocationOnMock -> {
            String nickname = invocationOnMock.getArgument(0);
            werewolvesGame.removePlayer(nickname);
            return null;
        }).when(werewolvesGameService).leaveGame(any(String.class), any(String.class));

        mvc.perform(post("/leave")
                .param("playerName", "nickname")
                .param("gameId", werewolvesGame.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        assertTrue(werewolvesGame.getPlayerFromNickname("nickname").isEmpty());
    }

    @Test
    public void givenUnknownPlayerLeaveGame_returns404() throws Exception {
        // given
        WerewolvesGame werewolvesGame = WerewolvesGame.createGame();
        werewolvesGame.addPlayer("nickname");

        // when
        doAnswer(invocationOnMock -> {
            String nickname = invocationOnMock.getArgument(0);
            werewolvesGame.removePlayer(nickname);
            return null;
        }).when(werewolvesGameService).leaveGame(any(String.class), any(String.class));


        // then
        mvc.perform(post("/leave")
                .param("playerName", "unknown")
                .param("gameId", werewolvesGame.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Game error\",\"details\":[\"The player was not found in the game.\"]}"));
        assertTrue(werewolvesGame.getPlayerFromNickname("nickname").isPresent());
    }

    @Test
    public void givenPlayerStartGame_returns200() throws Exception {
        // given
        WerewolvesGame werewolvesGame = createGameWith9Players();

        // when
        doAnswer(invocationOnMock -> {
            werewolvesGame.start();
            return null;
        }).when(werewolvesGameService).startGame(any(String.class), any(String.class));

        mvc.perform(post("/start")
                .param("playerName", "nickname")
                .param("gameId", werewolvesGame.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        assertTrue(werewolvesGame.isStarted());
    }

    @Test
    public void givenPlayerVote_returns200() throws Exception {
        // given
        WerewolvesGame werewolvesGame = createGameWith9Players();
        werewolvesGame.start();
        String firstWerewolf = werewolvesGame.getPlayers().stream().filter(Player::isWerewolf).findFirst().get().getNickname();
        String firstVillager = werewolvesGame.getPlayers().stream().filter(Player::isVillager).findFirst().get().getNickname();

        // when
        doAnswer(invocationOnMock -> {
            String voter = invocationOnMock.getArgument(0);
            String voted = invocationOnMock.getArgument(1);
            werewolvesGame.vote(voter, voted);
            return null;
        }).when(werewolvesGameService).vote(any(String.class), any(String.class), any(String.class));

        mvc.perform(post("/vote")
                .param("voterNickname", firstWerewolf)
                .param("votedNickname", firstVillager)
                .param("gameId", werewolvesGame.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // then
        assertEquals(1, werewolvesGame.getPlayerFromNickname(firstVillager).get().getVotedAgainst());
    }

    @Test
    public void givenPlayerGetStatus_returns200() throws Exception {
        // given
        WerewolvesGame werewolvesGame = WerewolvesGame.createGame();
        werewolvesGame.addPlayer("nickname");

        // when
        doAnswer(invocationOnMock -> {
            WerewolvesGameDTO werewolvesGameDTO = new WerewolvesGameDTO();
            werewolvesGameDTO.setId(werewolvesGame.getId());
            werewolvesGameDTO.setPhase(werewolvesGame.getPhase());
            werewolvesGameDTO.setWinner(werewolvesGame.getWinner());
            werewolvesGameDTO.setStarted(werewolvesGame.isStarted());
            werewolvesGameDTO.setPlayers(new ArrayList<>());
            werewolvesGame.getPlayers().forEach(player -> {
                PlayerDTO playerDTO = new PlayerDTO();
                playerDTO.setNickname(player.getNickname());
                playerDTO.setRole(player.getRole());
                playerDTO.setAlive(player.isAlive());
                playerDTO.setVotedAgainst(player.getVotedAgainst());
                werewolvesGameDTO.getPlayers().add(playerDTO);
            });
            return werewolvesGameDTO;
        }).when(werewolvesGameService).getStatus(any(String.class), any(String.class));

        // then
        mvc.perform(get("/status")
                .param("playerName", "nickname")
                .param("gameId", werewolvesGame.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(String.format("{\"id\":\"%s\",\"players\":[{\"nickname\":\"nickname\",\"role\":\"VILLAGER\",\"alive\":true,\"votedAgainst\":0}],\"started\":false,\"winner\":null,\"phase\":\"NIGHT\"}", werewolvesGame.getId())));
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