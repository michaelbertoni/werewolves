package com.thynkio.werewolvesserver.domain;

import com.thynkio.werewolvesserver.domain.exceptions.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@NoArgsConstructor
public class WerewolvesGame {
    private static final Logger logger = LoggerFactory.getLogger(WerewolvesGame.class);

    private String id;
    private List<Player> players;
    private boolean started;
    private Role winner;
    private Phase phase = Phase.NIGHT;

    private WerewolvesGame(String id, List<Player> players) {
        this.id = id;
        this.players = players;
    }

    public static WerewolvesGame createGame() {
        return new WerewolvesGame(UUID.randomUUID().toString(), new ArrayList<>());
    }

    public void start() throws GameException {
        if (players.size() < 4) {
            throw new InsufficientNumberOfPlayersException(id);
        }

        if (started) {
            throw new GameAlreadyStartedException(id);
        }

        logger.info("Werewolf game " + id + " starting");

        winner = null;
        players.forEach(Player::resurrect);
        assignRoleToPlayers();
        initializeNightPhase();
        started = true;

        logger.info("Werewolf game " + id + " started");
    }

    private void initializeNightPhase() {
        phase = Phase.NIGHT;
        players.forEach(Player::resetVote);
        logger.info("Werewolf game " + id + ": night phase started");
    }

    private void initializeDayPhase() {
        phase = Phase.DAY;
        players.forEach(Player::resetVote);
        logger.info("Werewolf game " + id + ": day phase started");
    }

    private void assignRoleToPlayers() {
        int numberOfWerewolves;
        if (players.size() < 12) {
            numberOfWerewolves = players.size() / 4;
        } else {
            numberOfWerewolves = 3;
        }
        while (countAliveWerewolves() < numberOfWerewolves) {
            ThreadLocalRandom.current()
                    .ints(0, players.size())
                    .distinct()
                    .limit(numberOfWerewolves)
                    .forEach(index -> {
                        players.get(index).setRole(Role.WEREWOLF);
                        logger.info("Werewolf game " + id + ": " + players.get(index).getNickname() + " is a werewolf");
                    });
        }
    }

    public void addPlayer(String nickname) throws GameException {
        if (getPlayerFromNickname(nickname).isPresent()) {
            throw new PlayersWithIdenticalNicknameException(id);
        }
        players.add(new Player(nickname));
        logger.info("Werewolf game " + id + ": " + nickname + " is added to the game");
    }

    public void removePlayer(String nickname) throws GameException {
        if (getPlayerFromNickname(nickname).isEmpty()) {
            throw new PlayerNotFoundInGameException(id);
        }
        players.removeIf(player -> player.getNickname().equals(nickname));
        logger.info("Werewolf game " + id + ": " + nickname + " is removed from the game");
    }

    public Optional<Player> getPlayerFromNickname(String nickname) {
        return players.stream()
                .filter(player -> player.getNickname().equals(nickname))
                .findFirst();
    }

    public void vote(String voterNickname, String votedNickname) throws GameException {
        if (!started) {
            throw new VoteBeforeGameStartsException(id);
        }

        if (getPlayerFromNickname(voterNickname).isEmpty()) {
            throw new VoteFromUnknownPlayerException(id);
        }

        if (getPlayerFromNickname(votedNickname).isEmpty()) {
            throw new VoteForUnknownPlayerException(id);
        }

        if (!getPlayerFromNickname(voterNickname).get().isAlive()) {
            throw new DeadPlayerVoterException(id);
        }

        if (!getPlayerFromNickname(votedNickname).get().isAlive()) {
            throw new DeadPlayerVotedException(id);
        }

        if (Phase.NIGHT.equals(phase) && getPlayerFromNickname(voterNickname).get().isVillager()) {
            throw new VillagerVoteDuringNightException(id);
        }

        if (Phase.NIGHT.equals(phase) && getPlayerFromNickname(voterNickname).get().isWerewolf()
                && getPlayerFromNickname(votedNickname).get().isWerewolf()) {
            throw new WerewolfVoteForOtherWerewolfException(id);
        }

        getPlayerFromNickname(votedNickname).get().addVote();
        logger.info("Werewolf game " + id + ": " + voterNickname + " voted to kill " + votedNickname);

        if (checkVoteCountEnd()) {
            logger.info("Werewolf game " + id + ": all players voted, computing results");
            endVote();
        }
    }

    private boolean checkVoteCountEnd() {
        switch (phase) {
            case NIGHT -> {
                return players.stream()
                        .map(Player::getVotedAgainst)
                        .reduce(0, Integer::sum)
                        .equals(countAliveWerewolves());
            }
            case DAY -> {
                return players.stream()
                        .map(Player::getVotedAgainst)
                        .reduce(0, Integer::sum)
                        .equals(countAlivePlayers());
            }
            default -> {
                return false;
            }
        }
    }

    private void endVote() {
        Optional<Player> electedPlayer = findElectedPlayerForElimination();
        if (electedPlayer.isPresent()) {
            electedPlayer.get().kill();
            logger.info("Werewolf game " + id + ": " + electedPlayer.get().getNickname() + " was killed");
        } else {
            logger.info("Werewolf game " + id + ": draw, no player gets killed");
        }
        if (checkEndgame()) {
            endGame();
        } else {
            nextPhase();
        }
    }

    private void nextPhase() {
        switch (phase) {
            case NIGHT -> initializeDayPhase();
            case DAY -> initializeNightPhase();
        }
    }

    private boolean checkEndgame() {
        if (countAliveWerewolves() == countAliveVillagers()) {
            winner = Role.WEREWOLF;
            logger.info("Werewolf game " + id + ": werewolves won the game");
            return true;
        }
        if (countAliveWerewolves() == 0) {
            winner = Role.VILLAGER;
            logger.info("Werewolf game " + id + ": villagers won the game");
            return true;
        }
        return false;
    }

    private void endGame() {
        started = false;
        logger.info("Werewolf game " + id + ": game ended");
    }

    private Optional<Player> findElectedPlayerForElimination() {
        Collections.sort(players);
        int highestVotedCount = players.get(0).getVotedAgainst();
        int secondHighestVotedCount = players.get(1).getVotedAgainst();
        if (highestVotedCount == secondHighestVotedCount) {
            return Optional.empty();
        } else {
            return Optional.of(players.get(0));
        }
    }

    public int countAliveWerewolves() {
        return players.stream()
                .filter(Player::isAlive)
                .filter(Player::isWerewolf)
                .map(e -> 1)
                .reduce(0, Integer::sum);
    }

    public int countAlivePlayers() {
        return players.stream()
                .filter(Player::isAlive)
                .map(e -> 1)
                .reduce(0, Integer::sum);
    }

    public int countAliveVillagers() {
        return players.stream()
                .filter(Player::isAlive)
                .filter(Player::isVillager)
                .map(e -> 1)
                .reduce(0, Integer::sum);
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }
}
