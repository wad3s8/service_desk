package com.wad3s.service_desk.dto.ticket;

import jakarta.validation.constraints.NotBlank;

public record CreateTicketCommentRequest(
        @NotBlank
        String text
) {}
