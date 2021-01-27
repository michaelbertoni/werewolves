package com.thynkio.werewolvesserver.game;

import com.thynkio.werewolvesserver.game.exceptions.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class WerewolvesGame {
    private static final Logger logger = LoggerFactory.getLogger(WerewolvesGame.class);

    private final String id;
    private final Map<String, Player> players;
    private boolean started;
    private Role winner;
    private Phase phase = Phase.NIGHT;
    private Map<String, Integer> voteCount = new HashMap<>();

    public WerewolvesGame() {
        this.id = UUID.randomUUID().toString();
        this.players = new HashMap<>();
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
        assignRoleToPlayers();
        initializeNightPhase();
        started = true;

        logger.info("Werewolf game " + id + " started");
    }

    private void initializeNightPhase() {
        phase = Phase.NIGHT;
        voteCount = new HashMap<>();
        getAliveVillagerNicknames().forEach(villagerNickname -> voteCount.put(villagerNickname, 0));
        logger.info("Werewolf game " + id + ": night phase started");
    }

    private void initializeDayPhase() {
        phase = Phase.DAY;
        voteCount = new HashMap<>();
        getAlivePlayerNicknames().forEach(playerNickname -> voteCount.put(playerNickname, 0));
        logger.info("Werewolf game " + id + ": day phase started");
    }

    private void assignRoleToPlayers() {
        List<String> playerNicknames = new ArrayList<>(players.keySet());
        int numberOfWerewolves;
        if (players.size() < 12) {
            numberOfWerewolves = players.size() / 4;
        } else {
            numberOfWerewolves = 3;
        }
        while (countAliveWerewolves() < numberOfWerewolves) {
            String randomPlayerNickname = playerNicknames.get(ThreadLocalRandom.current().nextInt(0, players.size()));
            if (players.get(randomPlayerNickname).isWerewolf()) {
                continue;
            }
            players.get(randomPlayerNickname).setRole(Role.WEREWOLF);
            logger.info("Werewolf game " + id + ": " + randomPlayerNickname + " is a werewolf");
        }
    }

    public void addPlayer(String nickname) throws GameException {
        if (players.containsKey(nickname)) {
            throw new PlayersWithIdenticalNicknameException(id);
        }
        players.put(nickname, new Player(nickname, id));
        logger.info("Werewolf game " + id + ": " + nickname + " is added to the game");
    }

    public void removePlayer(String nickname) throws GameException {
        if (!players.containsKey(nickname)) {
            throw new PlayerNotFoundForRemovalException(id);
        }
        players.remove(nickname);
        logger.info("Werewolf game " + id + ": " + nickname + " is removed from the game");
    }

    public void vote(String voterNickname, String votedNickname) throws GameException {
        if (!started) {
            throw new VoteBeforeGameStartsException(id);
        }

        if (!players.containsKey(voterNickname)) {
            throw new VoteFromUnknownPlayerException(id);
        }

        if (!players.containsKey(votedNickname)) {
            throw new VoteForUnknownPlayerException(id);
        }

        if (!players.get(voterNickname).isAlive()) {
            throw new DeadPlayerVoterException(id);
        }

        if (!players.get(votedNickname).isAlive()) {
            throw new DeadPlayerVotedException(id);
        }

        if (Phase.NIGHT.equals(phase) && players.get(voterNickname).isVillager()) {
            throw new VillagerVoteDuringNightException(id);
        }

        if (Phase.NIGHT.equals(phase) && players.get(voterNickname).isWerewolf() && players.get(votedNickname).isWerewolf()) {
            throw new WerewolfVoteForOtherWerewolfException(id);
        }

        int currentVoteCountForVoted = voteCount.get(votedNickname);
        voteCount.put(votedNickname, ++currentVoteCountForVoted);
        logger.info("Werewolf game " + id + ": " + voterNickname + " voted to kill " + votedNickname);

        if (checkVoteCountEnd()) {
            logger.info("Werewolf game " + id + ": all players voted, computing results");
            endVote();
        }
    }

    private boolean checkVoteCountEnd() {
        switch (phase) {
            case NIGHT -> {
                return voteCount.values()
                        .stream()
                        .reduce(0, Integer::sum)
                        .equals(countAliveWerewolves());
            }
            case DAY -> {
                return voteCount.values()
                        .stream()
                        .reduce(0, Integer::sum)
                        .equals(countAlivePlayers());
            }
            default -> {
                return false;
            }
        }
    }

    private void endVote() {
        String electedPlayerNickname = findElectedPlayerForElimination();
        if (!electedPlayerNickname.isEmpty()) {
            players.get(electedPlayerNickname).kill();
            logger.info("Werewolf game " + id + ": " + electedPlayerNickname + " was killed");
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

    private String findElectedPlayerForElimination() {
        List<String> highestVoteNicknames = new ArrayList<>();
        Integer highestVoteCount = 0;
        for (Map.Entry<String, Integer> entry : voteCount.entrySet()) {
            String nickname = entry.getKey();
            Integer votes = entry.getValue();
            if (votes > highestVoteCount) {
                highestVoteCount = votes;
                highestVoteNicknames = new ArrayList<>();
                highestVoteNicknames.add(nickname);
            } else if (votes.equals(highestVoteCount)) {
                highestVoteNicknames.add(nickname);
            }
        }

        if (highestVoteNicknames.size() > 1) {
            return "";
        } else return highestVoteNicknames.get(0);
    }

    public int countAliveWerewolves() {
        return (int) getPlayers()
                .values()
                .stream()
                .filter(Player::isAlive)
                .filter(Player::isWerewolf)
                .count();
    }

    public int countAlivePlayers() {
        return (int) getPlayers()
                .values()
                .stream()
                .filter(Player::isAlive)
                .count();
    }

    public int countAliveVillagers() {
        return (int) getPlayers()
                .values()
                .stream()
                .filter(Player::isAlive)
                .filter(Player::isVillager)
                .count();
    }

    public List<String> getAliveWerewolvesNicknames() {
        return players.values()
                .stream()
                .filter(Player::isAlive)
                .filter(Player::isWerewolf)
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    public List<String> getAliveVillagerNicknames() {
        return players.values()
                .stream()
                .filter(Player::isAlive)
                .filter(Player::isVillager)
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    public List<String> getAlivePlayerNicknames() {
        return players.values()
                .stream()
                .filter(Player::isAlive)
                .map(Player::getNickname)
                .collect(Collectors.toList());
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }
}
