package com.wad3s.service_desk.service;

import com.wad3s.service_desk.domain.*;
import com.wad3s.service_desk.dto.ticket.*;
import com.wad3s.service_desk.history.TicketHistoryAction;
import com.wad3s.service_desk.history.TicketHistoryService;
import com.wad3s.service_desk.repository.SubcategoryRepository;
import com.wad3s.service_desk.repository.TicketRepository;
import com.wad3s.service_desk.repository.UserRepository;
import com.wad3s.service_desk.sla.TicketSlaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceExecutor {

    private final TicketRepository ticketRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final TicketHistoryService historyService;
    private final TicketSlaService ticketSlaService;


    @Transactional(readOnly = true)
    public Ticket getTicketForExecutor(Long ticketId, Long executorId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + ticketId));

        if (ticket.getAssignee() == null ||
                !ticket.getAssignee().getId().equals(executorId)) {
            throw new AccessDeniedException("Ticket is not assigned to current executor");
        }

        return ticket;
    }

    @Transactional
    public TicketDto updateAsExecutor(Long id, TicketExecutorUpdateDto dto) {

        // 1. Текущий пользователь
        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        // 2. Тикет
        Ticket t = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + id));

        // 3. Проверка прав: текущий пользователь должен быть исполнителем тикета
        if (t.getAssignee() == null || !t.getAssignee().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Нет прав на изменение этого тикета (вы не исполнитель)");
        }

        // старые значения
        Subcategory oldSubcategory = t.getSubcategory();
        TicketStatus oldStatus = t.getStatus();


        // подкатегория
        if (dto.subcategoryId() != null) {
            Subcategory newSubcategory = subcategoryRepository.findById(dto.subcategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Subcategory not found: " + dto.subcategoryId()));

            if (oldSubcategory == null || !newSubcategory.getId().equals(oldSubcategory.getId())) {
                t.setSubcategory(newSubcategory);

                historyService.record(
                        t,
                        TicketHistoryAction.CATEGORY_CHANGED,
                        oldSubcategory != null ? oldSubcategory.getName() : null,
                        newSubcategory.getName(),
                        "Изменена категория исполнителем",
                        currentUser
                );

                // если SLA зависит от подкатегории — пересчитаем дедлайн
                // (если у тебя есть ticketSlaService)
                Instant now = Instant.now();
                t.setResolveDueAt(ticketSlaService.calcResolveDueAt(newSubcategory, now));

                // если раньше уже было нарушение, а дедлайн поменяли — логика на твой выбор:
                // 1) оставить resolveBreachedAt как факт нарушения
                // 2) сбросить если новый дедлайн в будущем (обычно НЕ сбрасывают)
            }
        }

        // cтатус
        if (dto.status() != null && dto.status() != oldStatus) {

            t.setStatus(dto.status());

            historyService.record(
                    t,
                    TicketHistoryAction.STATUS_CHANGED,
                    oldStatus.name(),
                    dto.status().name(),
                    "Статус изменён исполнителем",
                    currentUser
            );

            // если перешли в RESOLVED — ставим resolvedAt
            if (dto.status() == TicketStatus.RESOLVED && t.getResolvedAt() == null) {
                t.setResolvedAt(Instant.now());

                historyService.record(
                        t,
                        TicketHistoryAction.RESOLVED,
                        null,
                        null,
                        "Заявка решена исполнителем",
                        currentUser
                );
            }

            // если переоткрыли — сброс resolvedAt
            if (dto.status() == TicketStatus.REOPENED) {
                t.setResolvedAt(null);

                historyService.record(
                        t,
                        TicketHistoryAction.REOPENED,
                        null,
                        null,
                        "Заявка переоткрыта",
                        currentUser
                );
            }
        }

        // 7. Сохранение
        Ticket saved = ticketRepository.save(t);
        return TicketMapper.toDto(saved);
    }


    @Transactional
    public void delete(Long id) {

        // текущий пользователь
        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        // тикет
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + id));

        // только автор может отменить
        if (!ticket.getAssignee().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Нет прав на отмену этого тикета");
        }

        TicketStatus oldStatus = ticket.getStatus();

        // soft delete
        ticket.setStatus(TicketStatus.CANCELED);

        // история
        historyService.record(
                ticket,
                TicketHistoryAction.STATUS_CHANGED,
                oldStatus.name(),
                TicketStatus.CANCELED.name(),
                "Заявка отменена пользователем",
                currentUser
        );
    }

}
