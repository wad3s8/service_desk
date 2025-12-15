package com.wad3s.service_desk.controller;

import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.dto.ticket.ExecutorUpdateTicketRequest;
import com.wad3s.service_desk.service.TicketServiceCustomer;
import com.wad3s.service_desk.service.TicketServiceExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/executor/tickets")
@RequiredArgsConstructor
public class ExecutorTicketController {

    private final TicketServiceExecutor ticketServiceExecutor;

    // уже был
    @GetMapping("/my")
    @PreAuthorize("hasRole('executor')")
    public Page<Ticket> getMyTickets(Pageable pageable, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Long executorId = currentUser.getId();
        return ticketServiceExecutor.getTicketsForAssignee(executorId, pageable);
    }

    // НОВОЕ: получить один тикет
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('executor')")
    public Ticket getTicket(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Long executorId = currentUser.getId();
        return ticketServiceExecutor.getTicketForExecutor(id, executorId);
    }

    // НОВОЕ: частичное обновление тикета
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('executor')")
    public Ticket updateTicket(@PathVariable Long id,
                               @RequestBody ExecutorUpdateTicketRequest request,
                               Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Long executorId = currentUser.getId();
        return ticketServiceExecutor.updateTicketForExecutor(id, executorId, request);
    }
}
