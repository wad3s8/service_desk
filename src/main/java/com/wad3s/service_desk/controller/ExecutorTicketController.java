package com.wad3s.service_desk.controller;

import com.wad3s.service_desk.attachment.TicketQueryService;
import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.dto.ticket.TicketDto;
import com.wad3s.service_desk.dto.ticket.TicketExecutorUpdateDto;
import com.wad3s.service_desk.dto.ticket.TicketWithFilesDto;
import com.wad3s.service_desk.service.TicketServiceExecutor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/executor/tickets")
@RequiredArgsConstructor
public class ExecutorTicketController {

    private final TicketServiceExecutor ticketServiceExecutor;
    private final TicketQueryService ticketQueryService;


    @GetMapping("/my")
    @PreAuthorize("hasRole('executor')")
    public List<TicketWithFilesDto> getMyTicketsWithFiles() {
        return ticketQueryService.getAssignedTickets();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('executor')")
    public Ticket getTicket(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Long executorId = currentUser.getId();
        return ticketServiceExecutor.getTicketForExecutor(id, executorId);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('executor')")
    public TicketDto updateAsExecutor(
            @PathVariable Long id,
            @Valid @RequestBody TicketExecutorUpdateDto dto
    ) {
        return ticketServiceExecutor.updateAsExecutor(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('customer')")
    public void delete(@PathVariable Long id) {
        ticketServiceExecutor.delete(id);
    }
}
