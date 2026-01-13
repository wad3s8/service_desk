package com.wad3s.service_desk.calendar;

import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketCalendarService {

    private final TicketRepository ticketRepository;

    public TicketCalendarResponseDto getCalendar(
            LocalDate from,
            LocalDate to,
            ZoneId zoneId
    ) {

        Instant fromInstant = from.atStartOfDay(zoneId).toInstant();
        Instant toInstant = to.plusDays(1).atStartOfDay(zoneId).toInstant();

        List<Ticket> tickets =
                ticketRepository.findByResolveDueAtBetween(fromInstant, toInstant);

        // группируем по дню
        Map<LocalDate, List<TicketCalendarItemDto>> grouped =
                tickets.stream()
                        .filter(t -> t.getResolveDueAt() != null)
                        .collect(Collectors.groupingBy(
                                t -> t.getResolveDueAt()
                                        .atZone(zoneId)
                                        .toLocalDate(),
                                Collectors.mapping(
                                        t -> new TicketCalendarItemDto(
                                                t.getId(),
                                                t.getTitle()
                                        ),
                                        Collectors.toList()
                                )
                        ));

        List<TicketCalendarDayDto> days = from.datesUntil(to.plusDays(1))
                .map(date ->
                        new TicketCalendarDayDto(
                                date,
                                grouped.getOrDefault(date, List.of())
                        )
                )
                .toList();

        return new TicketCalendarResponseDto(from, to, days);
    }
}

