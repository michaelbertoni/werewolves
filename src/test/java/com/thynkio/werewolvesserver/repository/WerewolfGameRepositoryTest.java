package com.thynkio.werewolvesserver.repository;

import com.thynkio.werewolvesserver.domain.Role;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
class WerewolfGameRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WerewolvesGameRepository werewolvesGameRepository;

    @Test
    public void whenCreateGame_gameIsPersistedWithPlayer() {
        // given
        WerewolvesGameEntity werewolvesGameEntity = new WerewolvesGameEntity();
        werewolvesGameEntity.setId("testGame");
        werewolvesGameEntity.setStarted(false);
        werewolvesGameEntity.setWinner(Role.WEREWOLF);
        PlayerEntity playerEntity = new PlayerEntity();
        playerEntity.setNickname("testPlayer");
        playerEntity.setAlive(true);
        playerEntity.setVotedAgainst(0);
        playerEntity.setRole(Role.VILLAGER);
        playerEntity.setGame(werewolvesGameEntity);
        werewolvesGameEntity.setPlayers(Collections.singletonList(playerEntity));

        // when
        werewolvesGameRepository.save(werewolvesGameEntity);

        // then
        WerewolvesGameEntity entityFromEntityManager = entityManager.find(WerewolvesGameEntity.class, "testGame");
        assertEquals(entityFromEntityManager, werewolvesGameEntity);
        assertEquals(entityFromEntityManager.getPlayers().get(0), werewolvesGameEntity.getPlayers().get(0));
    }

}