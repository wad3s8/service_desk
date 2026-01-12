package com.wad3s.service_desk.repository;

import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByRequester(User requester);

    Page<Ticket> findByAssigneeId(Long assigneeId, Pageable pageable);

    List<Ticket> findAllByAssignee(User assignee);

    @Query("""
        select t from Ticket t
        where t.resolvedAt is null
          and t.resolveBreachedAt is null
          and t.resolveDueAt < :now
    """)
    List<Ticket> findNewlyBreached(@Param("now") Instant now);
}
