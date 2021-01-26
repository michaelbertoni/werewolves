package com.thynkio.werewolvesserver.game;

import com.thynkio.werewolvesserver.game.exceptions.InsufficientNumberOfPlayersException;
import com.thynkio.werewolvesserver.game.exceptions.PlayersWithIdenticalNicknameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WerewolvesGameTest {

    private static WerewolvesGame game;

    @BeforeEach
    void beforeEach() {
        game = new WerewolvesGame();
    }

    private static void addPlayersToGame(int numberOfPlayers) throws PlayersWithIdenticalNicknameException {
        for (int i = 0; i < numberOfPlayers; i++) {
            game.addPlayer(new Player("nickname" + i));
        }
    }

    @Test
    void gameHasAnUniqueId() {
        WerewolvesGame game2 = new WerewolvesGame();
        assertNotNull(game.getId());
        assertNotEquals(game.getId(), game2.getId());
    }

    @Test
    void gameCannotStartWithoutThreePlayers() throws PlayersWithIdenticalNicknameException {
        assertThrows(InsufficientNumberOfPlayersException.class, () -> game.start());
        addPlayersToGame(2);
        assertThrows(InsufficientNumberOfPlayersException.class, () -> game.start());
    }

    @Test
    void gameStartsWithExactlyThreePlayers() {
        assertDoesNotThrow(() -> {
            addPlayersToGame(3);
            game.start();
        });
    }

    @Test
    void gameStartsWithMoreThanThreePlayers() {
        assertDoesNotThrow(() -> {
            addPlayersToGame(7);
            game.start();
        });
    }

    @Test
    void cannotAddPlayerWithExistingNicknameInGame() {
        Player player1 = new Player("nickname");
        Player player2 = new Player("nickname");
        assertThrows(PlayersWithIdenticalNicknameException.class, () -> {
            game.addPlayer(player1);
            game.addPlayer(player2);
        });
    }
}
