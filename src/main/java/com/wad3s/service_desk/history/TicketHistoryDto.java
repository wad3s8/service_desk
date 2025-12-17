package com.wad3s.service_desk.history;

import java.time.Instant;

public record TicketHistoryDto(
        Long id,
        Long ticketId,
        TicketHistoryAction action,
        String oldValue,
        String newValue,
        String comment,
        String performedBy,
        Instant createdAt
) {}