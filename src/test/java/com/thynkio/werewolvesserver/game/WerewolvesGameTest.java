package com.thynkio.werewolvesserver.game;

import com.thynkio.werewolvesserver.game.exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    void gameHasNewPlayerAdded() throws GameException {
        WerewolvesGame game = new WerewolvesGame();
        game.addPlayer("Paul");
        Map<String, Player> playerHashMap = new HashMap<>();
        playerHashMap.put("Paul", new Player("Paul"));
        assertEquals(playerHashMap, game.getPlayers());
    }

    @Test
    void gameHasPlayerRemoved() throws GameException {
        WerewolvesGame game = new WerewolvesGame();
        game.addPlayer("Paul");
        game.addPlayer("Eric");
        game.removePlayer("Paul");
        Map<String, Player> playerHashMap = new HashMap<>();
        playerHashMap.put("Eric", new Player("Eric"));
        assertEquals(playerHashMap, game.getPlayers());
    }

    @Test
    void gameCannotRemoveUnknownPlayer() throws GameException {
        WerewolvesGame game = new WerewolvesGame();
        game.addPlayer("Paul");
        game.addPlayer("Eric");
        assertThrows(PlayerNotFoundForRemovalException.class, () -> game.removePlayer("George"));
        Map<String, Player> playerHashMap = new HashMap<>();
        playerHashMap.put("Paul", new Player("Paul"));
        playerHashMap.put("Eric", new Player("Eric"));
        assertEquals(playerHashMap, game.getPlayers());
    }

    @Test
    void gameCannotStartWithoutFourPlayers() throws GameException {
        WerewolvesGame game = new WerewolvesGame();
        assertThrows(InsufficientNumberOfPlayersException.class, game::start);
        addPlayersToGame(2, game);
        assertThrows(InsufficientNumberOfPlayersException.class, game::start);
    }

    @Test
    void gameStartsWithExactlyFourPlayers() {
        assertDoesNotThrow(() -> startNewGameWithFixedAmountOfPlayers(4));
    }

    @Test
    void gameStartsWithMoreThanFourPlayers() {
        assertDoesNotThrow(() -> startNewGameWithFixedAmountOfPlayers(7));
    }

    @Test
    void cannotAddPlayerWithExistingNicknameInGame() {
        WerewolvesGame game = new WerewolvesGame();
        assertThrows(PlayersWithIdenticalNicknameException.class, () -> {
            game.addPlayer("nickname");
            game.addPlayer("nickname");
        });
    }

    @Test
    void startedGameCannotBeRestarted() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(4);
        assertThrows(GameAlreadyStartedException.class, game::start);

    }

    @Test
    void playersGetARoleAssignedWhenAGameIsStarted() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(4);
        game.getPlayers().values().forEach(player -> assertNotNull(player.getRole()));
    }

    @Test
    void rightAmountOfWerewolvesIsSetForTheGame() throws GameException {
        WerewolvesGame game1 = startNewGameWithFixedAmountOfPlayers(4);
        int numberOfWerewolves1 = game1.countAliveWerewolves();
        assertEquals(1, numberOfWerewolves1);

        WerewolvesGame game2 = startNewGameWithFixedAmountOfPlayers(6);
        int numberOfWerewolves2 = game2.countAliveWerewolves();
        assertEquals(1, numberOfWerewolves2);

        WerewolvesGame game3 = startNewGameWithFixedAmountOfPlayers(8);
        int numberOfWerewolves3 = game3.countAliveWerewolves();
        assertEquals(2, numberOfWerewolves3);

        WerewolvesGame game4 = startNewGameWithFixedAmountOfPlayers(12);
        int numberOfWerewolves4 = game4.countAliveWerewolves();
        assertEquals(3, numberOfWerewolves4);

        WerewolvesGame game5 = startNewGameWithFixedAmountOfPlayers(20);
        int numberOfWerewolves5 = game5.countAliveWerewolves();
        assertEquals(3, numberOfWerewolves5);
    }

    @Test
    void gameShouldStartWithNightPhase() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(4);
        assertEquals(Phase.NIGHT, game.getPhase());
    }

    @Test
    void nobodyCanVoteIfTheGameHasNotStarted() throws GameException {
        WerewolvesGame game = new WerewolvesGame();
        game.addPlayer("nickname1");
        game.addPlayer("nickname3");
        assertThrows(VoteBeforeGameStartsException.class, () -> game.vote("nickname1", "nickname3"));
    }

    @Test
    void unknownPlayerCannotVoteDuringTheGame() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(4);
        assertThrows(VoteFromUnknownPlayerException.class, () -> game.vote("bonjour", "nickname3"));
    }

    @Test
    void cannotVoteForAnUnknownPlayerDuringTheGame() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(4);
        assertThrows(VoteForUnknownPlayerException.class, () -> game.vote("nickname1", "bonjour"));
    }

    @Test
    void voterCannotVoteWhenDead() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(4);
        String werewolfNickname = game.getPlayers().values().stream().filter(Player::isWerewolf).findFirst().get().getNickname();
        String villagerNickname = game.getPlayers().values().stream().filter(Player::isVillager).findFirst().get().getNickname();
        game.getPlayers().get(werewolfNickname).kill();
        assertThrows(DeadPlayerVoterException.class, () -> game.vote(werewolfNickname, villagerNickname));
    }

    @Test
    void voterCannotVoteForSomeoneDead() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(4);
        String werewolfNickname = game.getPlayers().values().stream().filter(Player::isWerewolf).findFirst().get().getNickname();
        String villagerNickname = game.getPlayers().values().stream().filter(Player::isVillager).findFirst().get().getNickname();
        game.getPlayers().get(villagerNickname).kill();
        assertThrows(DeadPlayerVotedException.class, () -> game.vote(werewolfNickname, villagerNickname));
    }

    @Test
    void villagerCannotVoteDuringNightPhase() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(4);
        String villagerNickname = game.getPlayers().values().stream().filter(Player::isVillager).findFirst().get().getNickname();
        assertThrows(VillagerVoteDuringNightException.class, () -> game.vote(villagerNickname, "nickname2"));
    }

    @Test
    void werewolfCannotVoteForAnotherWerewolfDuringNightPhase() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(4);
        String werewolfNickname = game.getPlayers().values().stream().filter(Player::isWerewolf).findFirst().get().getNickname();
        assertThrows(WerewolfVoteForOtherWerewolfException.class, () -> game.vote(werewolfNickname, werewolfNickname));
    }

    @Test
    void nightPhaseEndsWhenAllWerewolvesHaveVoted() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(4);
        List<String> werewolvesNicknames = game.getAliveWerewolvesNicknames();
        List<String> villagersNicknames = game.getAliveVillagerNicknames();
        for (String werewolfNickname : werewolvesNicknames) {
            game.vote(werewolfNickname, villagersNicknames.get(0));
        }
        assertEquals(Phase.DAY, game.getPhase());

        WerewolvesGame game2 = startNewGameWithFixedAmountOfPlayers(8);
        werewolvesNicknames = game2.getAliveWerewolvesNicknames();
        villagersNicknames = game2.getAliveVillagerNicknames();
        for (String werewolfNickname : werewolvesNicknames) {
            game2.vote(werewolfNickname, villagersNicknames.get(0));
        }
        assertEquals(Phase.DAY, game2.getPhase());
    }

    @Test
    void dayPhaseDoesNotEndWhenAllAlivePlayersHaveNotVoted() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(4);
        List<String> werewolvesNicknames = game.getAliveWerewolvesNicknames();
        List<String> villagersNicknames = game.getAliveVillagerNicknames();
        for (String werewolfNickname : werewolvesNicknames) {
            game.vote(werewolfNickname, villagersNicknames.get(0));
        }
        assertEquals(Phase.DAY, game.getPhase());

        werewolvesNicknames = game.getAliveWerewolvesNicknames();
        villagersNicknames = game.getAliveVillagerNicknames();
        game.vote(werewolvesNicknames.get(0), villagersNicknames.get(0));
        game.vote(villagersNicknames.get(0), villagersNicknames.get(0));
        assertEquals(Phase.DAY, game.getPhase());
    }

    @Test
    void electedPlayerIsKilledByWerewolvesDuringTheNight() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(8);
        List<String> werewolvesNicknames = game.getAliveWerewolvesNicknames();
        List<String> villagersNicknames = game.getAliveVillagerNicknames();
        for (String werewolfNickname : werewolvesNicknames) {
            game.vote(werewolfNickname, villagersNicknames.get(0));
        }
        assertFalse(game.getPlayers().get(villagersNicknames.get(0)).isAlive());
    }

    @Test
    void noPlayerIsKilledDuringTheNightIfVoteCountsEndsInDraw() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(8);
        List<String> werewolvesNicknames = game.getAliveWerewolvesNicknames();
        List<String> villagersNicknames = game.getAliveVillagerNicknames();
        game.vote(werewolvesNicknames.get(0), villagersNicknames.get(0));
        game.vote(werewolvesNicknames.get(1), villagersNicknames.get(1));
        villagersNicknames.forEach(villagerNickname -> assertTrue(game.getPlayers().get(villagerNickname).isAlive()));
    }

    @Test
    void gameEndsWhenWerewolfAmountEqualsVillagersAmount() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(4);
        // night 1
        List<String> werewolvesNicknames = game.getAliveWerewolvesNicknames();
        List<String> villagersNicknames = game.getAliveVillagerNicknames();
        game.vote(werewolvesNicknames.get(0), villagersNicknames.get(0));

        // day 1
        werewolvesNicknames = game.getAliveWerewolvesNicknames();
        villagersNicknames = game.getAliveVillagerNicknames();
        game.vote(werewolvesNicknames.get(0), villagersNicknames.get(0));
        game.vote(villagersNicknames.get(0), villagersNicknames.get(0));
        game.vote(villagersNicknames.get(1), werewolvesNicknames.get(0));

        assertEquals(Role.WEREWOLF, game.getWinner());
        assertFalse(game.isStarted());
    }

    @Test
    void gameEndsWhenVillagersKilledAllWerewolves() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(4);
        // night 1
        List<String> werewolvesNicknames = game.getAliveWerewolvesNicknames();
        List<String> villagersNicknames = game.getAliveVillagerNicknames();
        game.vote(werewolvesNicknames.get(0), villagersNicknames.get(0));

        // day 1
        werewolvesNicknames = game.getAliveWerewolvesNicknames();
        villagersNicknames = game.getAliveVillagerNicknames();
        game.vote(werewolvesNicknames.get(0), villagersNicknames.get(0));
        game.vote(villagersNicknames.get(0), werewolvesNicknames.get(0));
        game.vote(villagersNicknames.get(1), werewolvesNicknames.get(0));

        assertEquals(Role.VILLAGER, game.getWinner());
        assertFalse(game.isStarted());
    }

    @Test
    void gameContinuesIfWerewolvesAreRemainingAfterOneOfThemIsKilled() throws GameException {
        WerewolvesGame game = startNewGameWithFixedAmountOfPlayers(8);
        // night 1
        List<String> werewolvesNicknames = game.getAliveWerewolvesNicknames();
        List<String> villagersNicknames = game.getAliveVillagerNicknames();
        game.vote(werewolvesNicknames.get(0), villagersNicknames.get(0));
        game.vote(werewolvesNicknames.get(1), villagersNicknames.get(0));

        // day 1
        werewolvesNicknames = game.getAliveWerewolvesNicknames();
        villagersNicknames = game.getAliveVillagerNicknames();
        game.vote(werewolvesNicknames.get(0), villagersNicknames.get(0));
        game.vote(werewolvesNicknames.get(1), villagersNicknames.get(0));
        game.vote(villagersNicknames.get(0), werewolvesNicknames.get(0));
        game.vote(villagersNicknames.get(1), werewolvesNicknames.get(0));
        game.vote(villagersNicknames.get(2), werewolvesNicknames.get(0));
        game.vote(villagersNicknames.get(3), werewolvesNicknames.get(0));
        game.vote(villagersNicknames.get(4), werewolvesNicknames.get(0));

        assertNull(game.getWinner());
        assertTrue(game.isStarted());
        assertEquals(Phase.NIGHT, game.getPhase());
    }

    /**
     * Utility methods
     **/

    private static void addPlayersToGame(int numberOfPlayers, WerewolvesGame game) throws GameException {
        for (int i = 0; i < numberOfPlayers; i++) {
            game.addPlayer("nickname" + i);
        }
    }

    private WerewolvesGame startNewGameWithFixedAmountOfPlayers(int amountOfPlayers) throws GameException {
        WerewolvesGame game = new WerewolvesGame();
        addPlayersToGame(amountOfPlayers, game);
        game.start();
        return game;
    }
}
