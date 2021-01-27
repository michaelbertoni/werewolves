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

    @Test
    void testEqualsAndHashcode() {
        Player player1 = new Player("Eric");
        Player player2 = new Player("Eric");
        assertTrue(player1.equals(player2) && player2.equals(player1));
        assertEquals(player2.hashCode(), player1.hashCode());
    }
}
