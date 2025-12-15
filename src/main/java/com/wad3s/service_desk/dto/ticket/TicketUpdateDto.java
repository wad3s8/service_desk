package com.wad3s.service_desk.dto.ticket;

import com.wad3s.service_desk.domain.Location;
import com.wad3s.service_desk.domain.TicketPriority;
import com.wad3s.service_desk.domain.TicketStatus;
import jakarta.validation.constraints.Size;

/**
 * Все поля опциональны — изменяем только те, что не null.
 */
public record TicketUpdateDto(
        @Size(max = 255)
        String title,

        Location location,

        Long subcategoryId,

        TicketPriority priority,

        @Size(max = 512)
        String description,

        TicketStatus status,

        Long assigneeId   // опционально, если хочешь позволить менять исполнителя
) { }
