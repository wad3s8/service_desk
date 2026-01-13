package com.wad3s.service_desk.stats;

import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketStatsService {

    private final TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    public TicketStatsDto getStats(Instant from, Instant to) {

        long created = ticketRepository.countCreated(from, to);
        long active = ticketRepository.countActive();
        long closed = ticketRepository.countClosed(from, to);
        long breached = ticketRepository.countClosedSlaBreached(from, to);

        Double slaPercent = null;
        if (closed > 0) {
            slaPercent = ((double) (closed - breached) / closed) * 100.0;
        }

        List<Ticket> closedTickets = ticketRepository.findClosedForAvgTime(from, to);
        long avgSeconds = 0;
        if (!closedTickets.isEmpty()) {
            long total = 0;
            long count = 0;
            for (Ticket t : closedTickets) {
                total += java.time.Duration
                        .between(t.getCreatedAt(), t.getResolvedAt())
                        .getSeconds();
                count++;
            }
            avgSeconds = total / count;
        }

        return new TicketStatsDto(
                created,
                active,
                closed,
                breached,
                slaPercent,
                avgSeconds
        );
    }

}
