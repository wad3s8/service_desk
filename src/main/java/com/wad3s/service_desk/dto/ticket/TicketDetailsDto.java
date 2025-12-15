package com.wad3s.service_desk.dto.ticket;

import com.wad3s.service_desk.domain.TicketPriority;
import com.wad3s.service_desk.domain.TicketStatus;

import java.time.Instant;

public record TicketDetailsDto(
        Long id,
        String title,

        TicketStatus status,
        TicketPriority priority,

        Long locationId,
        String locationName,

        Long categoryId,
        String categoryName,

        Long subcategoryId,
        String subcategoryName,

        Long requesterId,
        String requesterName,

        Long assigneeId,
        String assigneeName,

        Long assignedTeamId,
        String assignedTeamName,

        String description,

        Instant createdAt,
        Instant updatedAt,
        Instant resolvedAt
) {}

