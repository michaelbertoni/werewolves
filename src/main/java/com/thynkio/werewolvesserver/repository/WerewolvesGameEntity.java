package com.thynkio.werewolvesserver.repository;

import com.thynkio.werewolvesserver.domain.Phase;
import com.thynkio.werewolvesserver.domain.Role;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity(name = "game")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WerewolvesGameEntity {
    @Id
    private String id;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerEntity> players;

    private boolean started;

    private Role winner;

    private Phase phase;
}
