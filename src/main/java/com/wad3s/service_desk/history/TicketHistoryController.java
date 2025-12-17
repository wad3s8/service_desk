package com.wad3s.service_desk.history;

import com.wad3s.service_desk.domain.User;
import com.wad3s.service_desk.service.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class TicketHistoryController {

    private final TicketHistoryQueryService historyService;
    private final CurrentUserService currentUserService;


    @GetMapping("/ticket/{ticketId}")
    public List<TicketHistoryDto> getTicketHistory(
            @PathVariable Long ticketId
    ) {
        return historyService.getHistoryByTicket(ticketId);
    }


    @GetMapping("/my")
    public List<TicketHistoryDto> getMyHistory() {
        User currentUser = currentUserService.getCurrentUser();
        return historyService.getHistoryByUser(currentUser);
    }
}
