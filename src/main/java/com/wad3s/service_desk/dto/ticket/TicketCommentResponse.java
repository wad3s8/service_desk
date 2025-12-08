package com.wad3s.service_desk.dto.ticket;

import java.time.Instant;

public record TicketCommentResponse(
        Long id,
        Long ticketId,
        Long authorId,
        String authorEmail,
        String text,
        boolean systemComment,
        Instant createdAt
) {}
