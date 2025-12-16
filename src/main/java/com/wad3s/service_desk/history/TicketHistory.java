package com.wad3s.service_desk.history;

import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "ticket_history")
@Getter
@Setter
@NoArgsConstructor
public class TicketHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // тикет
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "ticket_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_history_ticket")
    )
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketHistoryAction action;

    // универсально для статуса, приоритета, исполнителя и т.п.
    @Column(length = 255)
    private String oldValue;

    @Column(length = 255)
    private String newValue;

    // комментарий (причина отклонения, сообщение и т.д.)
    @Column(length = 512)
    private String comment;

    // кто выполнил действие
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "performed_by",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_history_user")
    )
    private User performedBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
