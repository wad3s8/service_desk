package com.wad3s.service_desk.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketStatsController {

    private final TicketStatsService ticketStatsService;

    @GetMapping("/stats")
    public TicketStatsDto getStats(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to
    ) {
        Instant now = Instant.now();
        Instant fromFinal = (from != null) ? from : now.minus(java.time.Duration.ofDays(2));
        Instant toFinal = (to != null) ? to : now;

        return ticketStatsService.getStats(fromFinal, toFinal);
    }
}

