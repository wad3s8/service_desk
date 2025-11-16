package com.wad3s.service_desk.dto.ticket;

import com.wad3s.service_desk.domain.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TicketCreateDto(
        @NotBlank
        @Size(max = 255)
        String title,

        @Size(max = 255)
        String location,

        @NotNull
        Long subcategoryId,

        TicketPriority priority,

        @NotBlank
        @Size(max = 512)
        String description
) { }