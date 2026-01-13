package com.wad3s.service_desk.aho_head;

import com.wad3s.service_desk.attachment.TicketQueryService;
import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.dto.TeamDto;
import com.wad3s.service_desk.dto.ticket.TicketDto;
import com.wad3s.service_desk.dto.ticket.TicketWithFilesDto;
import com.wad3s.service_desk.service.TicketServiceExecutor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aho")
@RequiredArgsConstructor
public class AhoTicketController {

        private final TicketServiceExecutor ticketServiceExecutor;
        private final TicketQueryService ticketQueryService;
        private final TicketServiceAxo ticketServiceAxo;


        @GetMapping("/tickets/my/received")
        @PreAuthorize("hasRole('aho_head')")
        public List<TicketWithFilesDto> getMyTicketsWithFiles() {
            return ticketQueryService.getAssignedTickets();
        }

    @GetMapping("/tickets/my/tickets-team")
    @PreAuthorize("hasRole('aho_head')")
    public List<TicketWithFilesDto> getMyTeamTicketsExceptMe() {
        return ticketQueryService.getMyTeamTicketsExceptMe();
    }

    @GetMapping("/member-teams")
    @PreAuthorize("hasRole('aho_head')")
    public TeamDto getMyTeam() {
        return ticketServiceAxo.getMyTeam();
    }


    @GetMapping("/tickets/{id}")
        @PreAuthorize("hasRole('aho_head')")
        public Ticket getTicket(@PathVariable Long id, Authentication authentication) {
            User currentUser = (User) authentication.getPrincipal();
            Long executorId = currentUser.getId();
            return ticketServiceExecutor.getTicketForExecutor(id, executorId);
        }

        @PatchMapping("/tickets/{id}")
        @PreAuthorize("hasRole('aho_head')")
        public TicketDto updateAsAho(
                @PathVariable Long id,
                @Valid @RequestBody TicketHeadUpdateDto dto
        ) {
            return ticketServiceAxo.updateAsAho(id, dto);
        }

        @DeleteMapping("/tickets/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @PreAuthorize("hasAnyRole('aho_head')")
        public void delete(@PathVariable Long id) {
            ticketServiceAxo.delete(id);
        }

}

