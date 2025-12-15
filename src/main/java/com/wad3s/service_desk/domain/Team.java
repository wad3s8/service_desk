package com.wad3s.service_desk.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String code; // AHO_MSK, IT_L1 и т.п.

    @Column(nullable = false)
    private boolean active = true;
}
