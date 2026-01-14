package com.wad3s.service_desk.calendar;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class TicketCalendarController {

    private final TicketCalendarService ticketCalendarService;

    // 1) календарь по teamId (фронт передаёт)
    @GetMapping("/teams/{teamId}/tickets")
    public TicketCalendarResponseDto getCalendarByTeam(
            @PathVariable Long teamId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(required = false, defaultValue = "UTC") String timezone
    ) {
        return ticketCalendarService.getCalendarByTeam(teamId, from, to, ZoneId.of(timezone));
    }

    // 2) календарь по команде текущего пользователя
    @GetMapping("/tickets/my-team")
    public TicketCalendarResponseDto getCalendarForMyTeam(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam(required = false, defaultValue = "UTC") String timezone
    ) {
        return ticketCalendarService.getCalendarForMyTeam(from, to, ZoneId.of(timezone));
    }
}
