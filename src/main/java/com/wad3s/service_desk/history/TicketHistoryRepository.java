package com.wad3s.service_desk.history;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketHistoryRepository
        extends JpaRepository<TicketHistory, Long> {

    List<TicketHistory> findByTicketIdOrderByCreatedAtAsc(Long ticketId);

    List<TicketHistory> findByPerformedByIdOrderByCreatedAtDesc(Long userId);
}