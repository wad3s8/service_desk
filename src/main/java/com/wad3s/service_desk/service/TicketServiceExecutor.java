package com.wad3s.service_desk.service;

import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.dto.ticket.ExecutorUpdateTicketRequest;
import com.wad3s.service_desk.repository.TicketRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketServiceExecutor {

    private final TicketRepository ticketRepository;


    public Page<Ticket> getTicketsForAssignee(Long assigneeId, Pageable pageable) {
        return ticketRepository.findByAssigneeId(assigneeId, pageable);
    }

    @Transactional(readOnly = true)
    public Ticket getTicketForExecutor(Long ticketId, Long executorId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + ticketId));

        if (ticket.getAssignee() == null ||
                !ticket.getAssignee().getId().equals(executorId)) {
            throw new AccessDeniedException("Ticket is not assigned to current executor");
        }

        return ticket;
    }

    @Transactional
    public Ticket updateTicketForExecutor(Long ticketId,
                                          Long executorId,
                                          ExecutorUpdateTicketRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + ticketId));

        if (ticket.getAssignee() == null ||
                !ticket.getAssignee().getId().equals(executorId)) {
            throw new AccessDeniedException("Ticket is not assigned to current executor");
        }

        // частичное обновление
        if (request.status() != null) {
            ticket.setStatus(request.status());

            // тут, при желании, можешь добавить логику установки resolvedAt
            // если статус перешёл в "RESOLVED"/"CLOSED" и т.п.
        }

        if (request.priority() != null) {
            ticket.setPriority(request.priority());
        }

        //if (request.location() != null) {
         //   ticket.setLocation(request.location());
        //}

        return ticketRepository.save(ticket);
    }

}
