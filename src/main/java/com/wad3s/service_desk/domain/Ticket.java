package com.wad3s.service_desk.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // кто создал
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "requester_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ticket_requester")
    )
    private User requester;

    // исполнитель (когда взяли в работу)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "assignee_id",
            foreignKey = @ForeignKey(name = "fk_ticket_assignee")
    )
    private User assignee;

    // назначенная группа (очередь)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "assigned_team_id",
            foreignKey = @ForeignKey(name = "fk_ticket_team")
    )
    private Team assignedTeam;

    // локация
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "location_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_ticket_location")
    )
    private Location location;

    // подкатегория
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "subcategory_id",
            foreignKey = @ForeignKey(name = "fk_ticket_subcategory")
    )
    private Subcategory subcategory;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketPriority priority = TicketPriority.MEDIUM;

    @NotBlank
    @Size(max = 512)
    @Column(nullable = false, length = 512)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketStatus status = TicketStatus.NEW;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private Instant resolvedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    @Transient
    public Category getCategory() {
        return subcategory != null ? subcategory.getCategory() : null;
    }
}
