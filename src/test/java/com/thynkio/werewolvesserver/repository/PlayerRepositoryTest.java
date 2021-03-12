package com.thynkio.werewolvesserver.repository;

import com.thynkio.werewolvesserver.domain.Role;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
class PlayerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void whenFindByNickname_thenReturnPlayer() {
        // given
        PlayerEntity newPlayerEntity = new PlayerEntity(null, "test", Role.VILLAGER, true, 0);
        entityManager.persist(newPlayerEntity);
        entityManager.flush();

        // when
        PlayerEntity findPlayerEntity = playerRepository.findByNickname("test");

        // then
        assertEquals(newPlayerEntity, findPlayerEntity);
    }

}