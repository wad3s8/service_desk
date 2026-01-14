package com.wad3s.service_desk.calendar;

import com.wad3s.service_desk.domain.TeamMember;
import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.repository.TeamMemberRepository;
import com.wad3s.service_desk.repository.TicketRepository;
import com.wad3s.service_desk.repository.UserRepository;
import com.wad3s.service_desk.service.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketCalendarService {

    private final TicketRepository ticketRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public TicketCalendarResponseDto getCalendarByTeam(
            Long teamId,
            LocalDate from,
            LocalDate to,
            ZoneId zoneId
    ) {

        List<User> teamUsers = teamMemberRepository.findAllByTeamIdAndActiveTrue(teamId).stream()
                .map(TeamMember::getUser)
                .toList();

        return buildCalendar(from, to, zoneId, teamUsers);
    }

    @Transactional(readOnly = true)
    public TicketCalendarResponseDto getCalendarForMyTeam(
            LocalDate from,
            LocalDate to,
            ZoneId zoneId
    ) {
        User currentUser = getCurrentUser();

        List<TeamMember> memberships = teamMemberRepository.findAllByUserIdAndActiveTrue(currentUser.getId());
        if (memberships.isEmpty()) {
            return new TicketCalendarResponseDto(from, to, List.of());
        }

        // если у юзера всегда одна команда — берём первую
        Long teamId = memberships.get(0).getTeam().getId();

        List<User> teamUsers = teamMemberRepository.findAllByTeamIdAndActiveTrue(teamId).stream()
                .map(TeamMember::getUser)
                .toList();

        return buildCalendar(from, to, zoneId, teamUsers);
    }

    private TicketCalendarResponseDto buildCalendar(
            LocalDate from,
            LocalDate to,
            ZoneId zoneId,
            List<User> assignees
    ) {
        if (assignees == null || assignees.isEmpty()) {
            return new TicketCalendarResponseDto(from, to, List.of());
        }

        Instant fromInstant = from.atStartOfDay(zoneId).toInstant();
        Instant toInstant = to.plusDays(1).atStartOfDay(zoneId).toInstant();

        List<Ticket> tickets = ticketRepository
                .findAllByResolveDueAtBetweenAndAssigneeIn(fromInstant, toInstant, assignees);

        Map<LocalDate, List<TicketCalendarItemDto>> grouped =
                tickets.stream()
                        .filter(t -> t.getResolveDueAt() != null)
                        .collect(Collectors.groupingBy(
                                t -> t.getResolveDueAt().atZone(zoneId).toLocalDate(),
                                Collectors.mapping(
                                        t -> new TicketCalendarItemDto(t.getId(), t.getTitle()),
                                        Collectors.toList()
                                )
                        ));

        List<TicketCalendarDayDto> days = from.datesUntil(to.plusDays(1))
                .map(date -> new TicketCalendarDayDto(date, grouped.getOrDefault(date, List.of())))
                .toList();

        return new TicketCalendarResponseDto(from, to, days);
    }

    private User getCurrentUser() {
        String email = currentUserService.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
    }
}
