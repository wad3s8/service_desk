package com.wad3s.service_desk.controller;

import com.wad3s.service_desk.dto.ticket.TicketCreateDto;
import com.wad3s.service_desk.dto.ticket.TicketDto;
import com.wad3s.service_desk.dto.ticket.TicketUpdateDto;
import com.wad3s.service_desk.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDto create(@Valid @RequestBody TicketCreateDto dto) {
        return ticketService.create(dto);
    }

    @PutMapping("/{id}")
    public TicketDto update(
            @PathVariable Long id,
            @Valid @RequestBody TicketUpdateDto dto
    ) {
        return ticketService.update(id, dto);
    }

    @GetMapping("/my")
    public List<TicketDto> getMyTickets(Pageable pageable) {
        return ticketService.getMyTickets();
    }
}
