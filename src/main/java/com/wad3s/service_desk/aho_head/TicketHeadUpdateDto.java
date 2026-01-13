package com.wad3s.service_desk.aho_head;


import com.wad3s.service_desk.domain.TicketPriority;
import com.wad3s.service_desk.domain.TicketStatus;

public record TicketHeadUpdateDto(

        Long locationId,
        Long subcategoryId,
        TicketStatus status,
        TicketPriority priority,
        Long assigneeId

) {}

