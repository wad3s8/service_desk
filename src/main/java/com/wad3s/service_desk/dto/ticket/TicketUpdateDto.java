package com.wad3s.service_desk.dto.ticket;

import com.wad3s.service_desk.domain.Location;
import com.wad3s.service_desk.domain.TicketPriority;
import com.wad3s.service_desk.domain.TicketStatus;
import jakarta.validation.constraints.Size;

public record TicketUpdateDto(
        @Size(max = 255)
        String title,

        Long locationId,

        Long subcategoryId,

        TicketPriority priority,

        @Size(max = 512)
        String description
) { }
