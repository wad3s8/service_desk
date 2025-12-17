package com.wad3s.service_desk.dto.ticket;

import com.wad3s.service_desk.domain.TicketPriority;
import com.wad3s.service_desk.domain.TicketStatus;

import java.time.Instant;

public record TicketDto(
        Long id,
        String title,

        Long locationId,
        String locationName,

        Long subcategoryId,
        String subcategoryName,

        Long categoryId,
        String categoryName,

        TicketPriority priority,
        TicketStatus status,
        String description,

        Long assigneeId,
        String assigneeName,
        String assigneeLastName,
        String assigneeEmail,


        Instant createdAt,
        Instant updatedAt,
        Instant resolvedAt
) {}


