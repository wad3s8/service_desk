package com.wad3s.service_desk.attachment;


public record TicketAttachmentDto(
        Long id,
        String filename,
        String contentType,
        long size
) {}

