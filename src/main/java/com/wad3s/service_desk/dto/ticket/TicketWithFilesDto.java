package com.wad3s.service_desk.dto.ticket;


import com.wad3s.service_desk.attachment.TicketAttachmentDto;
import com.wad3s.service_desk.domain.TicketPriority;
import com.wad3s.service_desk.domain.TicketStatus;
import java.time.Instant;
import java.util.List;

public record TicketWithFilesDto(
        Long id,
        String title,
        String description,
        TicketPriority priority,
        TicketStatus status,
        Instant createdAt,
        Instant updatedAt,
        Instant resolveDueAt,
        Instant resolveBreachedAt,
        List<TicketAttachmentDto> attachments
) {}
