package com.wad3s.service_desk.dto.ticket;

import com.wad3s.service_desk.domain.*;

public final class TicketMapper {

    private TicketMapper() {}

    public static TicketDto toDto(Ticket t) {

        Subcategory sub = t.getSubcategory();
        Category cat = sub != null ? sub.getCategory() : null;
        Location loc = t.getLocation();
        User assignee = t.getAssignee();

        return new TicketDto(
                t.getId(),
                t.getTitle(),

                loc != null ? loc.getId() : null,
                loc != null ? loc.getName() : null,

                sub != null ? sub.getId() : null,
                sub != null ? sub.getName() : null,

                cat != null ? cat.getId() : null,
                cat != null ? cat.getName() : null,

                t.getPriority(),
                t.getStatus(),
                t.getDescription(),

                assignee != null ? assignee.getId() : null,
                assignee != null ? assignee.getFirstName() : null,
                assignee != null ? assignee.getLastName() : null,
                assignee != null ? assignee.getEmail() : null,

                t.getCreatedAt(),
                t.getUpdatedAt(),
                t.getResolvedAt()
        );
    }
}
