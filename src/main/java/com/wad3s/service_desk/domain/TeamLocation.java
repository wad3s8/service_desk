package com.wad3s.service_desk.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "team_locations",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"team_id", "location_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Location location;

    // например: PRIMARY, BACKUP
    private String responsibilityType;
}
