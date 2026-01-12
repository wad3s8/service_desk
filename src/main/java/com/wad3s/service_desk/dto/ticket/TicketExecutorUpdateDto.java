package com.wad3s.service_desk.dto.ticket;

import com.wad3s.service_desk.domain.TicketPriority;
import com.wad3s.service_desk.domain.TicketStatus;

public record TicketExecutorUpdateDto(
        Long subcategoryId,
        TicketStatus status,
        TicketPriority priority) {}
