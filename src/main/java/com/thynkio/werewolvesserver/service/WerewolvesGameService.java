package com.thynkio.werewolvesserver.service;

import com.thynkio.werewolvesserver.repository.PlayerRepository;
import com.thynkio.werewolvesserver.repository.WerewolvesGameRepository;
import org.springframework.stereotype.Service;

@Service
public class WerewolvesGameService {

    public WerewolvesGameService(WerewolvesGameRepository werewolvesGameRepository,
                                 PlayerRepository playerRepository) {
    }
}
