package com.wad3s.service_desk.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "ticket_comments")
@Getter
@Setter
@NoArgsConstructor
public class TicketComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // к какому тикету относится
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "ticket_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_comment_ticket")
    )
    private Ticket ticket;

    // кто оставил комментарий
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "author_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_comment_author")
    )
    private User author;

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false, length = 2000)
    private String text;

    // системные комментарии (изменение статуса и т.д.)
    @Column(nullable = false)
    private boolean systemComment = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
