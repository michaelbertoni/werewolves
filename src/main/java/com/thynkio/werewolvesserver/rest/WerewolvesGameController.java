package com.thynkio.werewolvesserver.rest;

import com.thynkio.werewolvesserver.domain.exceptions.GameException;
import com.thynkio.werewolvesserver.service.WerewolvesGameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class WerewolvesGameController {

    private final WerewolvesGameService werewolvesGameService;

    public WerewolvesGameController(WerewolvesGameService werewolvesGameService) {
        this.werewolvesGameService = werewolvesGameService;
    }

    /**
     * Create a werewolves game
     *
     * @return game id
     */
    @PostMapping("/game")
    public @ResponseBody
    ResponseEntity<String> createGame() {
        return new ResponseEntity<>(this.werewolvesGameService.createGame(), HttpStatus.CREATED);
    }

    /**
     * Join a werewolves game as a player
     *
     * @param playerName the new player nickname
     * @param gameId     the game id of the existing joined game
     * @return game isStarted boolean
     */
    @PostMapping("/join")
    public @ResponseBody
    ResponseEntity<Boolean> joinGame(@RequestParam String playerName, @RequestParam String gameId) throws GameException {
        return new ResponseEntity<>(this.werewolvesGameService.joinGame(playerName, gameId), HttpStatus.OK);
    }

    /**
     * Leave a werewolves game as a player
     *
     * @param playerName the existing player nickname
     * @param gameId     the game id of the existing joined game
     * @return game isStarted boolean
     */
    @PostMapping("/leave")
    public @ResponseBody
    ResponseEntity<Void> leaveGame(@RequestParam String playerName, @RequestParam String gameId) throws GameException {
        this.werewolvesGameService.leaveGame(playerName, gameId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Vote against another player
     *
     * @param voterNickname the player nickname who votes
     * @param votedNickname the player nickname voted against
     * @param gameId        the existing game id
     * @return game phase
     */
    @PostMapping("/vote")
    public @ResponseBody
    ResponseEntity<String> vote(@RequestParam String voterNickname, @RequestParam String votedNickname, @RequestParam String gameId) throws GameException {
        return new ResponseEntity<>(this.werewolvesGameService.vote(voterNickname, votedNickname, gameId), HttpStatus.OK);
    }
}
