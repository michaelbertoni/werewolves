package com.thynkio.werewolvesserver.config;

import com.thynkio.werewolvesserver.domain.Role;
import com.thynkio.werewolvesserver.domain.WerewolvesGame;
import com.thynkio.werewolvesserver.domain.exceptions.GameException;
import com.thynkio.werewolvesserver.repository.PlayerEntity;
import com.thynkio.werewolvesserver.repository.WerewolvesGameEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ModelMapperConfiguration.class)
public class ModelMapperConfigurationTest {

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void whenMapFromWerewolves_returnWerewolvesEntityWithSameAttributes() throws GameException {
        // given
        WerewolvesGame werewolvesGame = WerewolvesGame.createGame();
        werewolvesGame.addPlayer("player1");

        // when
        WerewolvesGameEntity werewolvesGameEntity = modelMapper.map(werewolvesGame, WerewolvesGameEntity.class);

        // then
        assertEquals(werewolvesGame.getId(), werewolvesGameEntity.getId());
        assertNotNull(werewolvesGameEntity.getPlayers());
        assertNotEquals(0, werewolvesGameEntity.getPlayers().size());
        assertEquals("player1", werewolvesGameEntity.getPlayers().get(0).getNickname());
    }

    @Test
    public void whenMapFromWerewolvesEntity_returnWerewolvesWithSameAttributes() throws GameException {
        // given
        PlayerEntity playerEntity = new PlayerEntity();
        playerEntity.setNickname("testPlayer");
        WerewolvesGameEntity werewolvesGameEntity = new WerewolvesGameEntity();
        werewolvesGameEntity.setId("testGame");
        werewolvesGameEntity.setPlayers(Collections.singletonList(playerEntity));

        // when
        WerewolvesGame werewolvesGame = modelMapper.map(werewolvesGameEntity, WerewolvesGame.class);

        // then
        assertEquals(werewolvesGameEntity.getId(), werewolvesGame.getId());
        assertNotNull(werewolvesGame.getPlayers());
        assertNotEquals(0, werewolvesGame.getPlayers().size());
        assertEquals("testPlayer", werewolvesGame.getPlayers().get(0).getNickname());
    }
}
