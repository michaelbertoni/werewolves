package com.thynkio.werewolvesserver.service.dto;

import com.thynkio.werewolvesserver.domain.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlayerDTO {
    private String nickname;
    private Role role;
    private boolean alive;
    private int votedAgainst;
}
