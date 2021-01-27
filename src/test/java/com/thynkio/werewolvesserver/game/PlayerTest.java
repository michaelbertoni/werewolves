package com.thynkio.werewolvesserver.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    void testEqualsAndHashcode() {
        Player player1 = new Player("Eric", "id");
        Player player2 = new Player("Eric", "id");
        assertTrue(player1.equals(player2) && player2.equals(player1));
        assertEquals(player2.hashCode(), player1.hashCode());
    }
}
