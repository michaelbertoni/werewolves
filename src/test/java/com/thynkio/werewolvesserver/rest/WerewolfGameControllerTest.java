package com.thynkio.werewolvesserver.rest;

import com.thynkio.werewolvesserver.domain.WerewolvesGame;
import com.thynkio.werewolvesserver.service.WerewolvesGameService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(WerewolvesGameController.class)
class WerewolfGameControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private WerewolvesGameService werewolvesGameService;

    @Test
    public void givenPostGame_returnString() throws Exception {
        // given
        WerewolvesGame werewolvesGame = new WerewolvesGame();

        // when
        Mockito.when(werewolvesGameService.createGame()).thenReturn(werewolvesGame.getId());
        MvcResult result = mvc.perform(post("/game")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // then
        assertEquals(result.getResponse().getContentAsString(), werewolvesGame.getId());
    }

}