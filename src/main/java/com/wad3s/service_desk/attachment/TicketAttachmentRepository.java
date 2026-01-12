package com.wad3s.service_desk.attachment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketAttachmentRepository extends JpaRepository<TicketAttachment, Long> {
    List<TicketAttachment> findAllByTicketId(Long ticketId);

    List<TicketAttachment> findAllByTicketIdIn(List<Long> ticketIds);
}
