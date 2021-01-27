package com.thynkio.werewolvesserver.game;

import com.thynkio.werewolvesserver.game.exceptions.*;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class WerewolvesGame {
    private final String id;
    private final Map<String, Player> players;
    private boolean started;
    private Role winner;
    private Phase phase;
    private Map<String, Integer> voteCount;

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

        assignRoleToPlayers();
        initializeNightPhase();
        winner = null;
        started = true;
    }

    private void initializeNightPhase() {
        phase = Phase.NIGHT;
        voteCount = new HashMap<>();
        getAliveVillagerNicknames().forEach(villagerNickname -> voteCount.put(villagerNickname, 0));
    }

    private void initializeDayPhase() {
        phase = Phase.DAY;
        voteCount = new HashMap<>();
        getAlivePlayerNicknames().forEach(playerNickname -> voteCount.put(playerNickname, 0));
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
            players.get(randomPlayerNickname).setRole(Role.WEREWOLF);
        }
    }

    public void addPlayer(String nickname) throws GameException {
        if (players.containsKey(nickname)) {
            throw new PlayersWithIdenticalNicknameException(id);
        }
        players.put(nickname, new Player(nickname));
    }

    public void removePlayer(String nickname) throws GameException {
        if (!players.containsKey(nickname)) {
            throw new PlayerNotFoundForRemovalException(id);
        }
        players.remove(nickname);
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

        if (checkVoteCountEnd()) {
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
            return true;
        }
        if (countAliveWerewolves() == 0) {
            winner = Role.VILLAGER;
            return true;
        }
        return false;
    }

    private void endGame() {
        started = false;
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
}
