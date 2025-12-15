package com.wad3s.service_desk.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "team_member_skills",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"team_member_id", "subcategory_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "team_member_id",
            foreignKey = @ForeignKey(name = "fk_tms_member")
    )
    private TeamMember teamMember;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "subcategory_id",
            foreignKey = @ForeignKey(name = "fk_tms_subcategory")
    )
    private Subcategory subcategory;
}
