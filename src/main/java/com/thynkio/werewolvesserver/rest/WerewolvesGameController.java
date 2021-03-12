package com.thynkio.werewolvesserver.rest;

import com.thynkio.werewolvesserver.service.WerewolvesGameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WerewolvesGameController {

    private final WerewolvesGameService werewolvesGameService;

    public WerewolvesGameController(WerewolvesGameService werewolvesGameService) {
        this.werewolvesGameService = werewolvesGameService;
    }

    @PostMapping("/game")
    public @ResponseBody
    ResponseEntity<String> postGame() {
        return new ResponseEntity<>(this.werewolvesGameService.createGame(), HttpStatus.OK);
    }
}
