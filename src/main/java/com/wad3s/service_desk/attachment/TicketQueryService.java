package com.wad3s.service_desk.attachment;


import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.dto.ticket.TicketMapper;
import com.wad3s.service_desk.dto.ticket.TicketWithFilesDto;
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
}

