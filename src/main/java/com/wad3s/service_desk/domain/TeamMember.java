package com.wad3s.service_desk.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "team_members",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"team_id", "user_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamRole role;

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(
            mappedBy = "teamMember",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<TeamMemberSkill> skills = new HashSet<>();

}

