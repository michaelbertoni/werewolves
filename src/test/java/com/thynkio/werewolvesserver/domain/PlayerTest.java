package com.thynkio.werewolvesserver.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    void testEqualsAndHashcode() {
        Player player1 = new Player("Eric");
        Player player2 = new Player("Eric");
        assertTrue(player1.equals(player2) && player2.equals(player1));
        assertEquals(player2.hashCode(), player1.hashCode());
    }
}
