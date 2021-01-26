package com.thynkio.werewolvesserver.game;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private static Player player;

    @BeforeAll
    static void beforeAll() {
        player = new Player("nickname");
    }

    @Test
    void playerHasUniqueId() {
        Player player2 = new Player("nickname");
        assertNotNull(player.getId());
        assertNotEquals(player.getId(), player2.getId());
    }
}
