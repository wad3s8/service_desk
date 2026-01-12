package com.wad3s.service_desk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wad3s.service_desk.attachment.TicketQueryService;
import com.wad3s.service_desk.dto.ticket.TicketCreateDto;
import com.wad3s.service_desk.dto.ticket.TicketDto;
import com.wad3s.service_desk.dto.ticket.TicketUpdateDto;
import com.wad3s.service_desk.dto.ticket.TicketWithFilesDto;
import com.wad3s.service_desk.service.TicketServiceCustomer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/customer/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketServiceCustomer ticketServiceCustomer;
    private final ObjectMapper objectMapper;
    private final TicketQueryService ticketQueryService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('customer')")
    public TicketDto create(
            @RequestPart("data") String data,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        TicketCreateDto dto = objectMapper.readValue(data, TicketCreateDto.class);
        return ticketServiceCustomer.create(dto, files);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('customer')")
    public TicketDto updateJson(
            @PathVariable Long id,
            @Valid @RequestBody TicketUpdateDto dto
    ) {
        return ticketServiceCustomer.update(id, dto);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('customer')")
    public TicketDto updateMultipart(
            @PathVariable Long id,
            @RequestPart("data") String data,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        TicketUpdateDto dto = objectMapper.readValue(data, TicketUpdateDto.class);
        return ticketServiceCustomer.update(id, dto, files);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('customer')")
    public List<TicketWithFilesDto> getMyTickets() {
        return ticketQueryService.getMyTickets();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('customer')")
    public void delete(@PathVariable Long id) {
        ticketServiceCustomer.delete(id);
    }

}
