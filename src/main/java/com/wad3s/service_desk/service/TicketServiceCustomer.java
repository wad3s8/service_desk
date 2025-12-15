package com.wad3s.service_desk.service;

import com.wad3s.service_desk.domain.*;
import com.wad3s.service_desk.dto.ticket.*;
import com.wad3s.service_desk.repository.LocationRepository;
import com.wad3s.service_desk.repository.SubcategoryRepository;
import com.wad3s.service_desk.repository.TicketRepository;
import com.wad3s.service_desk.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceCustomer {

    private final TicketRepository ticketRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final LocationRepository locationRepository;
    private final TeamRoutingService teamRoutingService;
    private final AssignmentService  assignmentService;


    @Transactional
    public TicketDto create(TicketCreateDto dto) {

        // 1. Текущий пользователь
        String email = currentUserService.getCurrentUserEmail();
        User requester = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found: " + email)
                );

        // 2. Локация
        Location location = locationRepository.findById(dto.locationId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Location not found: " + dto.locationId()
                        )
                );

        // 3. Подкатегория
        Subcategory subcategory = subcategoryRepository.findById(dto.subcategoryId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Subcategory not found: " + dto.subcategoryId()
                        )
                );

        // 4. Поиск группы по иерархии
        Team assignedTeam = teamRoutingService
                .findTeamForLocation(location.getId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No team assigned for location: " + location.getId()
                        )
                );

        // 5. Подбор исполнителя по skills (или руководитель)
        User assignee = assignmentService
                .findAssignee(assignedTeam, subcategory);

        // 6. Создание тикета
        Ticket ticket = new Ticket();
        ticket.setRequester(requester);
        ticket.setSubcategory(subcategory);
        ticket.setLocation(location);
        ticket.setAssignedTeam(assignedTeam);
        ticket.setAssignee(assignee);

        ticket.setTitle(dto.title());
        ticket.setDescription(dto.description());
        ticket.setPriority(
                dto.priority() != null ? dto.priority() : TicketPriority.MEDIUM
        );

        // 7. Статус
        if (assignee != null) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        } else {
            ticket.setStatus(TicketStatus.NEW);
        }

        // 8. Сохранение
        Ticket saved = ticketRepository.save(ticket);

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
