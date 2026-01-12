package com.wad3s.service_desk.sla;

import com.wad3s.service_desk.domain.Subcategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(
        name = "subcategory_sla",
        uniqueConstraints = @UniqueConstraint(
                name = "ux_subcategory_sla_subcategory",
                columnNames = "subcategory_id"
        )
)
@Getter
@Setter
@NoArgsConstructor
public class SubcategorySla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "subcategory_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_subcategory_sla_subcategory")
    )
    private Subcategory subcategory;

    @Column(nullable = false)
    private long resolveMinutes;
}



