package com.thynkio.werewolvesserver.service;

import com.thynkio.werewolvesserver.repository.PlayerRepository;
import com.thynkio.werewolvesserver.repository.WerewolvesGameRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WerewolvesGameServiceImpl.class})
class WerewolfGameServiceTest {

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private WerewolvesGameRepository werewolvesGameRepository;

    @MockBean
    private PlayerRepository playerRepository;

    @Autowired
    private WerewolvesGameService werewolvesGameService;

    @Test
    public void whenCreateGame_gameIdIsReturned() {
        // given

        // when
        String newGameId = werewolvesGameService.createGame();

        // then
        Logger.getLogger(WerewolfGameServiceTest.class.getName()).info(newGameId);
        assertNotNull(newGameId);
        assertNotEquals(newGameId, "");
    }
}