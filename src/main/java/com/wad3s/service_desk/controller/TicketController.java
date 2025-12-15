package com.wad3s.service_desk.controller;

import com.wad3s.service_desk.dto.ticket.TicketCreateDto;
import com.wad3s.service_desk.dto.ticket.TicketDto;
import com.wad3s.service_desk.dto.ticket.TicketUpdateDto;
import com.wad3s.service_desk.service.TicketServiceCustomer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketServiceCustomer ticketServiceCustomer;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('customer')")
    public TicketDto create(@Valid @RequestBody TicketCreateDto dto) {
        return ticketServiceCustomer.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('customer')")
    public TicketDto update(
            @PathVariable Long id,
            @Valid @RequestBody TicketUpdateDto dto
    ) {
        return ticketServiceCustomer.update(id, dto);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('customer')")
    public List<TicketDto> getMyTickets(Pageable pageable) {
        return ticketServiceCustomer.getMyTickets();
    }
}
