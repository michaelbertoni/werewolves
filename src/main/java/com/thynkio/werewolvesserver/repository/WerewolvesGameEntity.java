package com.thynkio.werewolvesserver.repository;

import com.thynkio.werewolvesserver.domain.Phase;
import com.thynkio.werewolvesserver.domain.Role;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WerewolvesGameEntity {
    @Id
    private String id;

    @OneToMany(targetEntity = PlayerEntity.class, mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlayerEntity> players;

    private boolean started;

    private Role winner;

    private Phase phase;
}
