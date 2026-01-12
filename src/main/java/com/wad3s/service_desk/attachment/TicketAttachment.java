package com.wad3s.service_desk.attachment;

import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "ticket_attachments")
@Getter
@Setter
@NoArgsConstructor
public class TicketAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_attachment_ticket"))
    private Ticket ticket;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false, unique = true)
    private String storageKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploaded_by", nullable = false,
            foreignKey = @ForeignKey(name = "fk_attachment_uploaded_by"))
    private User uploadedBy;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    @PrePersist
    void onCreate() {
        uploadedAt = Instant.now();
    }
}
