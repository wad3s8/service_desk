package com.wad3s.service_desk.aho_head;

import com.wad3s.service_desk.domain.*;
import com.wad3s.service_desk.dto.TeamDto;
import com.wad3s.service_desk.dto.TeamMemberDto;
import com.wad3s.service_desk.dto.ticket.TicketDto;
import com.wad3s.service_desk.dto.ticket.TicketMapper;
import com.wad3s.service_desk.history.TicketHistoryAction;
import com.wad3s.service_desk.history.TicketHistoryService;
import com.wad3s.service_desk.repository.*;
import com.wad3s.service_desk.service.CurrentUserService;
import com.wad3s.service_desk.sla.TicketSlaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceAxo {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final TicketHistoryService historyService;
    private final TicketSlaService ticketSlaService;
    private final LocationRepository locationRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public TicketDto updateAsAho(Long id, TicketHeadUpdateDto dto) {

        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        Ticket t = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + id));

        Subcategory oldSubcategory = t.getSubcategory();
        TicketStatus oldStatus = t.getStatus();
        TicketPriority oldPriority = t.getPriority();
        User oldAssignee = t.getAssignee();
        Location oldLocation = t.getLocation();

        // location
        if (dto.locationId() != null) {

            Location newLocation = locationRepository.findById(dto.locationId())
                    .orElseThrow(() -> new EntityNotFoundException("Location not found: " + dto.locationId()));

            boolean changed = oldLocation == null || !oldLocation.getId().equals(newLocation.getId());
            if (changed) {
                t.setLocation(newLocation);

                historyService.record(
                        t,
                        TicketHistoryAction.LOCATION_CHANGED,
                        oldLocation != null ? oldLocation.getName() : null,
                        newLocation.getName(),
                        "Изменена локация",
                        currentUser
                );
            }
        }

        // subcategory
        if (dto.subcategoryId() != null) {
            Subcategory newSubcategory = subcategoryRepository.findById(dto.subcategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Subcategory not found: " + dto.subcategoryId()));

            boolean changed = oldSubcategory == null || !newSubcategory.getId().equals(oldSubcategory.getId());
            if (changed) {
                t.setSubcategory(newSubcategory);

                historyService.record(
                        t,
                        TicketHistoryAction.CATEGORY_CHANGED,
                        oldSubcategory != null ? oldSubcategory.getName() : null,
                        newSubcategory.getName(),
                        "Изменена категория исполнителем",
                        currentUser
                );

                Instant now = Instant.now();
                t.setResolveDueAt(ticketSlaService.calcResolveDueAt(newSubcategory, now));
            }
        }

        // priority
        if (dto.priority() != null && dto.priority() != oldPriority) {
            t.setPriority(dto.priority());

            historyService.record(
                    t,
                    TicketHistoryAction.PRIORITY_CHANGED,
                    oldPriority != null ? oldPriority.name() : null,
                    dto.priority().name(),
                    "Приоритет изменён исполнителем",
                    currentUser
            );

            if (t.getSubcategory() != null) {
                Instant now = Instant.now();
                t.setResolveDueAt(ticketSlaService.calcResolveDueAt(t.getSubcategory(), now));
            }
        }

        // assignee
        if (dto.assigneeId() != null) {
            User newAssignee = userRepository.findById(dto.assigneeId())
                    .orElseThrow(() -> new EntityNotFoundException("Assignee not found: " + dto.assigneeId()));

            boolean changed = oldAssignee == null || !newAssignee.getId().equals(oldAssignee.getId());
            if (changed) {
                t.setAssignee(newAssignee);

                historyService.record(
                        t,
                        TicketHistoryAction.ASSIGNEE_CHANGED,
                        oldAssignee != null ? oldAssignee.getEmail() : null,
                        newAssignee.getEmail(),
                        "Исполнитель изменён",
                        currentUser
                );
            }
        }

        // status
        if (dto.status() != null && dto.status() != oldStatus) {
            t.setStatus(dto.status());

            historyService.record(
                    t,
                    TicketHistoryAction.STATUS_CHANGED,
                    oldStatus != null ? oldStatus.name() : null,
                    dto.status().name(),
                    "Статус изменён исполнителем",
                    currentUser
            );

            if (dto.status() == TicketStatus.RESOLVED && t.getResolvedAt() == null) {
                t.setResolvedAt(Instant.now());

                historyService.record(
                        t,
                        TicketHistoryAction.RESOLVED,
                        null,
                        null,
                        "Заявка решена исполнителем",
                        currentUser
                );
            }

            if (dto.status() == TicketStatus.REOPENED) {
                t.setResolvedAt(null);

                historyService.record(
                        t,
                        TicketHistoryAction.REOPENED,
                        null,
                        null,
                        "Заявка переоткрыта",
                        currentUser
                );
            }
        }

        Ticket saved = ticketRepository.save(t);
        return TicketMapper.toDto(saved);
    }


    @Transactional
    public void delete(Long id) {

        // текущий пользователь
        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        // тикет
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + id));


        TicketStatus oldStatus = ticket.getStatus();

        // soft delete
        ticket.setStatus(TicketStatus.CANCELED);

        // история
        historyService.record(
                ticket,
                TicketHistoryAction.STATUS_CHANGED,
                oldStatus.name(),
                TicketStatus.CANCELED.name(),
                "Заявка отменена пользователем",
                currentUser
        );
    }

    @Transactional(readOnly = true)
    public TeamDto getMyTeam() {

        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        // membership текущего пользователя
        List<TeamMember> myMemberships = teamMemberRepository.findAllByUserIdAndActiveTrue(currentUser.getId());
        if (myMemberships.isEmpty()) {
            throw new EntityNotFoundException("Current user is not in any active team");
        }

        // выбор команды (если вдруг несколько)
        TeamMember chosen = myMemberships.stream()
                .filter(m -> m.getRole() == TeamRole.MANAGER)   // поменяй на нужную роль
                .findFirst()
                .orElse(myMemberships.get(0));

        Team team = chosen.getTeam();

        // все участники команды
        List<TeamMemberDto> members = teamMemberRepository.findAllByTeamIdAndActiveTrue(team.getId()).stream()
                .map(tm -> new TeamMemberDto(
                        tm.getUser().getId(),
                         tm.getUser().getFirstName() + " " + tm.getUser().getLastName(),   // поменяй при необходимости
                        tm.getUser().getEmail(),
                        tm.getRole()
                ))
                .toList();

        return new TeamDto(
                team.getId(),
                team.getName(),
                team.getCode(),
                team.isActive(),
                members
        );
    }


}


