package com.wad3s.service_desk.history;

public class TicketHistoryMapper {

    public static TicketHistoryDto toDto(TicketHistory h) {
        return new TicketHistoryDto(
                h.getId(),
                h.getTicket().getId(),
                h.getAction(),
                h.getOldValue(),
                h.getNewValue(),
                h.getComment(),
                h.getPerformedBy().getEmail(),
                h.getCreatedAt()
        );
    }
}