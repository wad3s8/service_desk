package com.wad3s.service_desk.attachment;


import com.wad3s.service_desk.domain.*;
import com.wad3s.service_desk.dto.ticket.TicketMapper;
import com.wad3s.service_desk.dto.ticket.TicketWithFilesDto;
import com.wad3s.service_desk.repository.TeamMemberRepository;
import com.wad3s.service_desk.repository.TeamRepository;
import com.wad3s.service_desk.repository.TicketRepository;
import com.wad3s.service_desk.repository.UserRepository;
import com.wad3s.service_desk.service.CurrentUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketQueryService {

    private final TicketRepository ticketRepository;
    private final TicketAttachmentRepository attachmentRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    private static final List<TicketStatus> FINISHED_STATUSES = List.of(
            TicketStatus.RESOLVED,
            TicketStatus.CLOSED,
            TicketStatus.CANCELED
    );

    @Transactional(readOnly = true)
    public List<TicketWithFilesDto> getMyTickets() {

        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        return ticketRepository.findAllByRequester(currentUser).stream()
                .map(t -> {
                    List<TicketAttachmentDto> files =
                            attachmentRepository.findAllByTicketId(t.getId()).stream()
                                    .map(a -> new TicketAttachmentDto(
                                            a.getId(),
                                            a.getFilename(),
                                            a.getContentType(),
                                            a.getSize()
                                    ))
                                    .toList();

                    return TicketMapper.toWithFilesDto(t, files);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketWithFilesDto> getAssignedTickets() {

        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        return ticketRepository.findAllByAssignee(currentUser).stream()
                .map(t -> {
                    List<TicketAttachmentDto> files =
                            attachmentRepository.findAllByTicketId(t.getId()).stream()
                                    .map(a -> new TicketAttachmentDto(
                                            a.getId(),
                                            a.getFilename(),
                                            a.getContentType(),
                                            a.getSize()
                                    ))
                                    .toList();

                    return TicketMapper.toWithFilesDto(t, files);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketWithFilesDto> getMyFinishedTickets() {

        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        List<TicketStatus> finishedStatuses = List.of(
                TicketStatus.RESOLVED,
                TicketStatus.CLOSED,
                TicketStatus.CANCELED
        );

        return ticketRepository
                .findAllByAssigneeAndStatusIn(currentUser, finishedStatuses)
                .stream()
                .map(t -> {
                    List<TicketAttachmentDto> files =
                            attachmentRepository.findAllByTicketId(t.getId()).stream()
                                    .map(a -> new TicketAttachmentDto(
                                            a.getId(),
                                            a.getFilename(),
                                            a.getContentType(),
                                            a.getSize()
                                    ))
                                    .toList();

                    return TicketMapper.toWithFilesDto(t, files);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketWithFilesDto> getMyArchivedTickets() {

        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        return ticketRepository
                .findAllByAssigneeAndStatusIn(currentUser, FINISHED_STATUSES)
                .stream()
                .map(t -> {
                    List<TicketAttachmentDto> files =
                            attachmentRepository.findAllByTicketId(t.getId()).stream()
                                    .map(a -> new TicketAttachmentDto(
                                            a.getId(),
                                            a.getFilename(),
                                            a.getContentType(),
                                            a.getSize()
                                    ))
                                    .toList();

                    return TicketMapper.toWithFilesDto(t, files);
                })
                .toList();
    }



    @Transactional(readOnly = true)
    public List<TicketWithFilesDto> getMyActiveTickets() {

        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        List<TicketStatus> activeStatuses = List.of(
                TicketStatus.NEW,
                TicketStatus.IN_PROGRESS
        );

        return ticketRepository
                .findAllByAssigneeAndStatusIn(currentUser, activeStatuses)
                .stream()
                .map(t -> {
                    List<TicketAttachmentDto> files =
                            attachmentRepository.findAllByTicketId(t.getId()).stream()
                                    .map(a -> new TicketAttachmentDto(
                                            a.getId(),
                                            a.getFilename(),
                                            a.getContentType(),
                                            a.getSize()
                                    ))
                                    .toList();

                    return TicketMapper.toWithFilesDto(t, files);
                })
                .toList();
    }


    @Transactional(readOnly = true)
    public List<TicketWithFilesDto> getMyTeamTicketsExceptMeActive() {

        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        List<TicketStatus> activeStatuses = List.of(
                TicketStatus.NEW,
                TicketStatus.IN_PROGRESS
        );

        List<TeamMember> myMemberships = teamMemberRepository.findAllByUserIdAndActiveTrue(currentUser.getId());
        if (myMemberships.isEmpty()) {
            throw new EntityNotFoundException("Current user is not in any active team");
        }

        TeamMember chosen = myMemberships.stream()
                .filter(m -> m.getRole() == TeamRole.MANAGER)
                .findFirst()
                .orElse(myMemberships.get(0));

        Long teamId = chosen.getTeam().getId();

        List<User> teamUsersExceptMe = teamMemberRepository.findAllByTeamIdAndActiveTrue(teamId).stream()
                .map(TeamMember::getUser)
                .filter(u -> u != null && !u.getId().equals(currentUser.getId()))
                .toList();

        if (teamUsersExceptMe.isEmpty()) return List.of();

        List<Ticket> tickets = ticketRepository
                .findAllByAssigneeInAndAssigneeNotAndStatusIn(teamUsersExceptMe, currentUser, activeStatuses);

        return tickets.stream()
                .map(t -> {
                    List<TicketAttachmentDto> files =
                            attachmentRepository.findAllByTicketId(t.getId()).stream()
                                    .map(a -> new TicketAttachmentDto(
                                            a.getId(),
                                            a.getFilename(),
                                            a.getContentType(),
                                            a.getSize()
                                    ))
                                    .toList();

                    return TicketMapper.toWithFilesDto(t, files);
                })
                .toList();
    }



}

