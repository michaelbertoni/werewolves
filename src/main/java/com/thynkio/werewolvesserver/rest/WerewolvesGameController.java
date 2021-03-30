package com.thynkio.werewolvesserver.rest;

import com.thynkio.werewolvesserver.service.WerewolvesGameService;
import com.thynkio.werewolvesserver.service.dto.WerewolvesGameDTO;
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
    ResponseEntity<String> createGame() throws Exception {
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
    ResponseEntity<Void> joinGame(@RequestParam String playerName, @RequestParam String gameId) throws Exception {
        this.werewolvesGameService.joinGame(playerName, gameId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Leave a werewolves game as a player
     *
     * @param playerName the existing player nickname
     * @param gameId     the game id of the existing joined game
     */
    @PostMapping("/leave")
    public @ResponseBody
    ResponseEntity<Void> leaveGame(@RequestParam String playerName, @RequestParam String gameId) throws Exception {
        this.werewolvesGameService.leaveGame(playerName, gameId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Starts a werewolves game
     *
     * @param playerName the existing player nickname requesting
     * @param gameId     the game id of the existing joined game
     */
    @PostMapping("/start")
    public @ResponseBody
    ResponseEntity<Void> startGame(@RequestParam String playerName, @RequestParam String gameId) throws Exception {
        this.werewolvesGameService.startGame(playerName, gameId);
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
    ResponseEntity<Void> vote(@RequestParam String voterNickname, @RequestParam String votedNickname, @RequestParam String gameId) throws Exception {
        this.werewolvesGameService.vote(voterNickname, votedNickname, gameId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Retrieve status of the game
     *
     * @param gameId     the existing game id
     * @param playerName the nickname of the player requesting status
     * @return a JSON representation of the werewolves game
     */
    @GetMapping("/status")
    public @ResponseBody
    ResponseEntity<WerewolvesGameDTO> getStatus(@RequestParam String gameId, @RequestParam String playerName) throws Exception {
        return new ResponseEntity<>(this.werewolvesGameService.getStatus(gameId, playerName), HttpStatus.OK);
    }
}
