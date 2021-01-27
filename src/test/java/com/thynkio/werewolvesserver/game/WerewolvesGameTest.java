package com.thynkio.werewolvesserver.game;

import com.thynkio.werewolvesserver.game.exceptions.GameAlreadyStartedException;
import com.thynkio.werewolvesserver.game.exceptions.GameException;
import com.thynkio.werewolvesserver.game.exceptions.InsufficientNumberOfPlayersException;
import com.thynkio.werewolvesserver.game.exceptions.PlayersWithIdenticalNicknameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WerewolvesGameTest {

    @Test
    void gameHasAnUniqueId() {
        WerewolvesGame game = new WerewolvesGame();
        WerewolvesGame game2 = new WerewolvesGame();
        assertNotNull(game.getId());
        assertNotEquals(game.getId(), game2.getId());
    }

    @Test
    void gameCannotStartWithoutThreePlayers() throws GameException {
        WerewolvesGame game = new WerewolvesGame();
        assertThrows(InsufficientNumberOfPlayersException.class, game::start);
        addPlayersToGame(2, game);
        assertThrows(InsufficientNumberOfPlayersException.class, game::start);
    }

    @Test
    void gameStartsWithExactlyThreePlayers() {
        assertDoesNotThrow(() -> startNewGameWithFixedAmountOfPlayers(3));
    }

    @Test
    void gameStartsWithMoreThanThreePlayers() {
        assertDoesNotThrow(() -> startNewGameWithFixedAmountOfPlayers(7));
    }

    @Test
    void cannotAddPlayerWithExistingNicknameInGame() {
        WerewolvesGame game = new WerewolvesGame();
        Player player1 = new Player("nickname");
        Player player2 = new Player("nickname");
        assertThrows(PlayersWithIdenticalNicknameException.class, () -> {
            game.addPlayer(player1);
            game.addPlayer(player2);
        });
    }

    @Test
    void startedGameCannotBeRestarted() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(3);
        assertThrows(GameAlreadyStartedException.class, game::start);

    }

    @Test
    void playersGetARoleAssignedWhenAGameIsStarted() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(3);
        game.getPlayers().values().forEach(player -> assertNotNull(player.getRole()));
    }

    @Test
    void rightAmountOfWerewolvesIsSetForTheGame() throws GameException {
        WerewolvesGame game1 = startNewGameWithFixedAmountOfPlayers(3);
        int numberOfWerewolves1 = game1.getWerewolvesCountInGame();
        assertEquals(1, numberOfWerewolves1);

        WerewolvesGame game2 = startNewGameWithFixedAmountOfPlayers(6);
        int numberOfWerewolves2 = game2.getWerewolvesCountInGame();
        assertEquals(2, numberOfWerewolves2);

        WerewolvesGame game3 = startNewGameWithFixedAmountOfPlayers(11);
        int numberOfWerewolves3 = game3.getWerewolvesCountInGame();
        assertEquals(2, numberOfWerewolves3);

        WerewolvesGame game4 = startNewGameWithFixedAmountOfPlayers(12);
        int numberOfWerewolves4 = game4.getWerewolvesCountInGame();
        assertEquals(3, numberOfWerewolves4);
    }

    /**
     * Utility methods
     **/

    private static void addPlayersToGame(int numberOfPlayers, WerewolvesGame game) throws GameException {
        for (int i = 0; i < numberOfPlayers; i++) {
            game.addPlayer(new Player("nickname" + i));
        }
    }

    private WerewolvesGame startNewGameWithFixedAmountOfPlayers(int amountOfPlayers) throws GameException {
        WerewolvesGame game = new WerewolvesGame();
        addPlayersToGame(amountOfPlayers, game);
        game.start();
        return game;
    }
}
