package com.wad3s.service_desk.service;

import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.domain.TicketComment;
import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.dto.ticket.CreateTicketCommentRequest;
import com.wad3s.service_desk.dto.ticket.TicketCommentResponse;
import com.wad3s.service_desk.repository.TicketCommentRepository;
import com.wad3s.service_desk.repository.TicketRepository;
import com.wad3s.service_desk.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketCommentService {

    private final TicketCommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository; // если есть, иначе бери User из SecurityContext

    @Transactional(readOnly = true)
    public List<TicketCommentResponse> getCommentsByTicket(Long ticketId) {
        List<TicketComment> comments = commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
        return comments.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TicketCommentResponse addComment(Long ticketId, Long authorId, CreateTicketCommentRequest req) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + ticketId));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + authorId));

        TicketComment comment = new TicketComment();
        comment.setTicket(ticket);
        comment.setAuthor(author);
        comment.setText(req.text());
        comment.setSystemComment(false);

        TicketComment saved = commentRepository.save(comment);
        return toResponse(saved);
    }

    private TicketCommentResponse toResponse(TicketComment c) {
        return new TicketCommentResponse(
                c.getId(),
                c.getTicket().getId(),
                c.getAuthor().getId(),
                c.getAuthor().getEmail(),
                c.getText(),
                c.isSystemComment(),
                c.getCreatedAt()
        );
    }
}
