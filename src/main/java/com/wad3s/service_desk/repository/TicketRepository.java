package com.wad3s.service_desk.repository;

import com.wad3s.service_desk.domain.Ticket;
import com.wad3s.service_desk.domain.TicketStatus;
import com.wad3s.service_desk.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByRequester(User requester);

    List<Ticket> findByResolveDueAtBetween(
            Instant from,
            Instant to
    );

    Page<Ticket> findByAssigneeId(Long assigneeId, Pageable pageable);

    List<Ticket> findAllByAssignee(User assignee);

    List<Ticket> findAllByAssigneeIn(Collection<User> assignees);

    @Query("""
select count(t) from Ticket t
where t.createdAt >= :from and t.createdAt < :to
""")
    long countCreated(Instant from, Instant to);

    @Query("""
select count(t) from Ticket t
where t.status <> com.wad3s.service_desk.domain.TicketStatus.RESOLVED
""")
    long countActive();

    @Query("""
select count(t) from Ticket t
where t.resolvedAt is not null
  and t.resolvedAt >= :from and t.resolvedAt < :to
""")
    long countClosed(Instant from, Instant to);

    @Query("""
select count(t) from Ticket t
where t.resolvedAt is not null
  and t.resolvedAt >= :from and t.resolvedAt < :to
  and t.resolveBreachedAt is not null
""")
    long countClosedSlaBreached(Instant from, Instant to);

    @Query("""
select t from Ticket t
where t.resolvedAt is not null
  and t.resolvedAt >= :from and t.resolvedAt < :to
  and t.createdAt is not null
""")
    List<Ticket> findClosedForAvgTime(Instant from, Instant to);

    List<Ticket> findAllByAssigneeAndStatusIn(User assignee, Collection<TicketStatus> statuses);

    List<Ticket> findAllByAssigneeInAndAssigneeNot(Collection<User> assignees, User excluded);

    @Query("""
        select t from Ticket t
        where t.resolvedAt is null
          and t.resolveBreachedAt is null
          and t.resolveDueAt < :now
    """)
    List<Ticket> findNewlyBreached(@Param("now") Instant now);

    List<Ticket> findAllByResolveDueAtBetweenAndAssigneeIn(
            Instant from,
            Instant to,
            Collection<User> assignees
    );
}
