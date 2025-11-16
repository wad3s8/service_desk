package com.wad3s.service_desk.dto.ticket;

import com.wad3s.service_desk.domain.Category;
import com.wad3s.service_desk.domain.Subcategory;
import com.wad3s.service_desk.domain.Ticket;

public final class TicketMapper {

    private TicketMapper() { }

    public static TicketDto toDto(Ticket t) {
        Subcategory sub = t.getSubcategory();
        Category cat = sub != null ? sub.getCategory() : null;

        return new TicketDto(
                t.getId(),
                t.getTitle(),
                t.getLocation(),
                sub != null ? sub.getId() : null,
                sub != null ? sub.getName() : null,
                cat != null ? cat.getId() : null,
                cat != null ? cat.getName() : null,
                t.getPriority(),
                t.getStatus(),
                t.getDescription(),
                t.getCreatedAt(),
                t.getUpdatedAt(),
                t.getResolvedAt()
        );
    }
}
