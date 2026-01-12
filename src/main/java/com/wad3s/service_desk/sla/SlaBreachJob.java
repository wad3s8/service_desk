package com.wad3s.service_desk.sla;


import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.history.TicketHistoryAction;
import com.wad3s.service_desk.history.TicketHistoryService;
import com.wad3s.service_desk.repository.TicketRepository;
import com.wad3s.service_desk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SlaBreachJob {

    private final TicketRepository ticketRepository;
    private final TicketHistoryService historyService;
    private final UserRepository userRepository;

    private static final String SYSTEM_EMAIL = "system@service-desk";

    @Scheduled(fixedDelayString = "PT1M") // раз в минуту после завершения прошлого запуска
    @Transactional
    public void markResolveBreaches() {
        Instant now = Instant.now();

        User systemUser = userRepository.findByEmail(SYSTEM_EMAIL)
                .orElseThrow(() -> new IllegalStateException("System user not found: " + SYSTEM_EMAIL));


        List<Ticket> breached = ticketRepository.findNewlyBreached(now);

        for (Ticket t : breached) {
            t.setResolveBreachedAt(now);

            historyService.record(
                    t,
                    TicketHistoryAction.SLA_BREACHED,
                    "OK",
                    "BREACHED",
                    "Нарушено SLA по времени решения. Дедлайн был: " + t.getResolveDueAt(),
                    systemUser
            );
        }

    }
}
