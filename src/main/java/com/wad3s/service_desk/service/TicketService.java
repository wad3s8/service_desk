package com.wad3s.service_desk.service;

import com.wad3s.service_desk.domain.*;
import com.wad3s.service_desk.dto.ticket.*;
import com.wad3s.service_desk.repository.SubcategoryRepository;
import com.wad3s.service_desk.repository.TicketRepository;
import com.wad3s.service_desk.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

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

        if (request.location() != null) {
            ticket.setLocation(request.location());
        }

        return ticketRepository.save(ticket);
    }

    @Transactional
    public TicketDto create(TicketCreateDto dto) {
        String email = currentUserService.getCurrentUserEmail();
        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        Subcategory subcategory = subcategoryRepository.findById(dto.subcategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Subcategory not found: " + dto.subcategoryId()));

        Ticket t = new Ticket();
        t.setRequester(requester);
        t.setSubcategory(subcategory);
        t.setTitle(dto.title());
        t.setLocation(dto.location());
        t.setPriority(dto.priority() != null ? dto.priority() : TicketPriority.MEDIUM);
        t.setDescription(dto.description());
        t.setStatus(TicketStatus.NEW);

        Ticket saved = ticketRepository.save(t);
        return TicketMapper.toDto(saved);
    }

    @Transactional
    public TicketDto update(Long id, TicketUpdateDto dto) {
        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        Ticket t = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + id));

        // разрешаем редактировать только автору (дальше можно добавить проверку на роль админа)
        if (!t.getRequester().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Нет прав на изменение этого тикета");
        }

        if (dto.title() != null) t.setTitle(dto.title());
        if (dto.location() != null) t.setLocation(dto.location());

        if (dto.subcategoryId() != null) {
            Subcategory subcategory = subcategoryRepository.findById(dto.subcategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Subcategory not found: " + dto.subcategoryId()));
            t.setSubcategory(subcategory);
        }

        if (dto.priority() != null) t.setPriority(dto.priority());
        if (dto.description() != null) t.setDescription(dto.description());

        if (dto.status() != null) {
            t.setStatus(dto.status());
            if ((dto.status() == TicketStatus.RESOLVED || dto.status() == TicketStatus.CLOSED)
                    && t.getResolvedAt() == null) {
                t.setResolvedAt(Instant.now());
            }
        }

        // назначение исполнителя (если хочешь это разрешать из этого метода)
        if (dto.assigneeId() != null) {
            User assignee = userRepository.findById(dto.assigneeId())
                    .orElseThrow(() -> new EntityNotFoundException("Assignee not found: " + dto.assigneeId()));
            t.setAssignee(assignee);
        }

        Ticket saved = ticketRepository.save(t);
        return TicketMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<TicketDto> getMyTickets() {
        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        return ticketRepository.findAllByRequester(currentUser).stream()
                .map(TicketMapper::toDto)
                .toList();
    }

}
