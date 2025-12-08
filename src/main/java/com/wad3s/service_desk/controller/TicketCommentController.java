package com.wad3s.service_desk.controller;

import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.dto.ticket.CreateTicketCommentRequest;
import com.wad3s.service_desk.dto.ticket.TicketCommentResponse;
import com.wad3s.service_desk.service.TicketCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/tickets/{ticketId}/comments")
@RequiredArgsConstructor
public class TicketCommentController {

    private final TicketCommentService commentService;

    // получить все комментарии тикета
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<TicketCommentResponse> getComments(@PathVariable Long ticketId) {
        return commentService.getCommentsByTicket(ticketId);
    }

    // добавить комментарий
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public TicketCommentResponse addComment(@PathVariable Long ticketId,
                                            @Valid @RequestBody CreateTicketCommentRequest req,
                                            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Long authorId = currentUser.getId();
        return commentService.addComment(ticketId, authorId, req);
    }
}
