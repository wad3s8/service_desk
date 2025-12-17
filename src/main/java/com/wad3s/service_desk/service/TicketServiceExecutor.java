package com.wad3s.service_desk.service;

import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.dto.ticket.ExecutorUpdateTicketRequest;
import com.wad3s.service_desk.dto.ticket.TicketDto;
import com.wad3s.service_desk.dto.ticket.TicketMapper;
import com.wad3s.service_desk.repository.TicketRepository;
import com.wad3s.service_desk.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceExecutor {

    private final TicketRepository ticketRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public List<TicketDto> getTicketsForAssignee() {
        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
        return ticketRepository.findAllByAssignee(currentUser).stream()
                .map(TicketMapper::toDto)
                .toList();
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
