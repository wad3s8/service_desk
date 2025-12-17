package com.wad3s.service_desk.history;

import com.wad3s.service_desk.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketHistoryQueryService {

    private final TicketHistoryRepository historyRepository;

    public List<TicketHistoryDto> getHistoryByTicket(Long ticketId) {
        return historyRepository
                .findByTicketIdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(TicketHistoryMapper::toDto)
                .toList();
    }

    public List<TicketHistoryDto> getHistoryByUser(User user) {
        return historyRepository
                .findByPerformedByIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(TicketHistoryMapper::toDto)
                .toList();
    }
}
