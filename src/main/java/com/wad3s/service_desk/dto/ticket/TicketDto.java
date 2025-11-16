package com.wad3s.service_desk.dto.ticket;

import com.wad3s.service_desk.domain.TicketPriority;
import com.wad3s.service_desk.domain.TicketStatus;

import java.time.Instant;

public record TicketDto(
        Long id,
        String title,
        String location,
        Long subcategoryId,
        String subcategoryName,
        Long categoryId,
        String categoryName,
        TicketPriority priority,
        TicketStatus status,
        String description,
        Instant createdAt,
        Instant updatedAt,
        Instant resolvedAt
) { }
