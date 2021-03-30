package com.thynkio.werewolvesserver.service.dto;

import com.thynkio.werewolvesserver.domain.Phase;
import com.thynkio.werewolvesserver.domain.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WerewolvesGameDTO {
    private String id;
    private List<PlayerDTO> players;
    private boolean started;
    private Role winner;
    private Phase phase;
}
