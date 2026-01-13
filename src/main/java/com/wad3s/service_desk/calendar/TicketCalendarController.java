package com.wad3s.service_desk.calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/tickets/calendar")
@RequiredArgsConstructor
public class TicketCalendarController {

    private final TicketCalendarService ticketCalendarService;

    @GetMapping
    public TicketCalendarResponseDto getCalendar(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(required = false, defaultValue = "UTC") String timezone
    ) {
        ZoneId zoneId = ZoneId.of(timezone);
        return ticketCalendarService.getCalendar(from, to, zoneId);
    }
}
