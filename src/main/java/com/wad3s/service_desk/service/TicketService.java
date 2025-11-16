package com.wad3s.service_desk.service;

import com.wad3s.service_desk.domain.*;
import com.wad3s.service_desk.dto.ticket.TicketCreateDto;
import com.wad3s.service_desk.dto.ticket.TicketDto;
import com.wad3s.service_desk.dto.ticket.TicketMapper;
import com.wad3s.service_desk.dto.ticket.TicketUpdateDto;
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
