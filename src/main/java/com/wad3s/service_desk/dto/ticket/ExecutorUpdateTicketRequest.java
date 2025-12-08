package com.wad3s.service_desk.dto.ticket;

import com.wad3s.service_desk.domain.TicketPriority;
import com.wad3s.service_desk.domain.TicketStatus;

public record ExecutorUpdateTicketRequest(
        TicketStatus status,
        TicketPriority priority,
        String location
) {
}
