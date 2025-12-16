package com.wad3s.service_desk.history;

import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketHistoryService {

    private final TicketHistoryRepository repository;

    public void record(
            Ticket ticket,
            TicketHistoryAction action,
            String oldValue,
            String newValue,
            String comment,
            User performedBy
    ) {
        TicketHistory history = new TicketHistory();
        history.setTicket(ticket);
        history.setAction(action);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setComment(comment);
        history.setPerformedBy(performedBy);

        repository.save(history);
    }
}

