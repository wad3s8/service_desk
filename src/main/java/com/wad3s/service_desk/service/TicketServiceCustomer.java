package com.wad3s.service_desk.service;

import com.wad3s.service_desk.attachment.FileStorageService;
import com.wad3s.service_desk.attachment.TicketAttachment;
import com.wad3s.service_desk.attachment.TicketAttachmentDto;
import com.wad3s.service_desk.attachment.TicketAttachmentRepository;
import com.wad3s.service_desk.domain.*;
import com.wad3s.service_desk.dto.ticket.*;
import com.wad3s.service_desk.history.TicketHistoryAction;
import com.wad3s.service_desk.history.TicketHistoryService;
import com.wad3s.service_desk.repository.LocationRepository;
import com.wad3s.service_desk.repository.SubcategoryRepository;
import com.wad3s.service_desk.repository.TicketRepository;
import com.wad3s.service_desk.repository.UserRepository;
import com.wad3s.service_desk.sla.TicketSlaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketServiceCustomer {

    private final TicketRepository ticketRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final LocationRepository locationRepository;
    private final TeamRoutingService teamRoutingService;
    private final AssignmentService  assignmentService;
    private final TicketHistoryService historyService;
    private final TicketSlaService ticketSlaService;
    private final FileStorageService fileStorageService;
    private final TicketAttachmentRepository attachmentRepository;


    @Transactional
    public TicketDto create(TicketCreateDto dto, List<MultipartFile> files) {

        // 1. Текущий пользователь
        String email = currentUserService.getCurrentUserEmail();
        User requester = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException("User not found: " + email)
                );

        // 2. Локация
        Location location = locationRepository.findById(dto.locationId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Location not found: " + dto.locationId()
                        )
                );

        // 3. Подкатегория
        Subcategory subcategory = subcategoryRepository.findById(dto.subcategoryId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Subcategory not found: " + dto.subcategoryId()
                        )
                );

        // 4. Поиск группы по иерархии
        Team assignedTeam = teamRoutingService
                .findTeamForLocation(location.getId())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No team assigned for location: " + location.getId()
                        )
                );

        // 5. Подбор исполнителя по skills (или руководитель)
        User assignee = assignmentService
                .findAssignee(assignedTeam, subcategory);

        // 6. Создание тикета
        Ticket ticket = new Ticket();
        ticket.setRequester(requester);
        ticket.setSubcategory(subcategory);
        ticket.setLocation(location);
        ticket.setAssignedTeam(assignedTeam);
        ticket.setAssignee(assignee);

        ticket.setTitle(dto.title());
        ticket.setDescription(dto.description());
        ticket.setPriority(
                dto.priority() != null ? dto.priority() : TicketPriority.MEDIUM
        );

        // 7. Статус
        if (assignee != null) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        } else {
            ticket.setStatus(TicketStatus.NEW);
        }

        Instant now = Instant.now();
        ticket.setResolveDueAt(ticketSlaService.calcResolveDueAt(subcategory, now));

        // 8. Сохранение
        Ticket saved = ticketRepository.save(ticket);

        // 8.1 Вложения
        if (files != null && !files.isEmpty()) {
            for (MultipartFile f : files) {
                if (f.isEmpty()) continue;

                String storageKey = fileStorageService.save(f, saved.getId());

                TicketAttachment att = new TicketAttachment();
                att.setTicket(saved);
                att.setFilename(
                        f.getOriginalFilename() != null ? f.getOriginalFilename() : "file"
                );
                att.setContentType(
                        f.getContentType() != null ? f.getContentType() : "application/octet-stream"
                );
                att.setSize(f.getSize());
                att.setStorageKey(storageKey);
                att.setUploadedBy(requester);

                attachmentRepository.save(att);
            }
        }


        // 9. История
        historyService.record(
                saved,
                TicketHistoryAction.CREATED,
                null,
                saved.getStatus().name(),
                "Заявка создана пользователем",
                requester
        );

        if (assignee != null) {
            historyService.record(
                    saved,
                    TicketHistoryAction.ASSIGNEE_CHANGED,
                    null,
                    assignee.getEmail(),
                    "Исполнитель назначен автоматически",
                    requester
            );

            historyService.record(
                    saved,
                    TicketHistoryAction.STATUS_CHANGED,
                    TicketStatus.NEW.name(),
                    TicketStatus.IN_PROGRESS.name(),
                    "Заявка взята в работу автоматически",
                    requester
            );
        }

        return TicketMapper.toDto(saved);

    }


    @Transactional(readOnly = true)
    public TicketWithFilesDto getTicketForCustomer(Long ticketId, Long customerId) {
        Ticket t = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + ticketId));

        if (t.getRequester() == null || !t.getRequester().getId().equals(customerId)) {
            throw new AccessDeniedException("Ticket does not belong to current customer");
        }

        List<TicketAttachmentDto> files =
                attachmentRepository.findAllByTicketId(t.getId()).stream()
                        .map(a -> new TicketAttachmentDto(
                                a.getId(),
                                a.getFilename(),
                                a.getContentType(),
                                a.getSize()
                        ))
                        .toList();

        return TicketMapper.toWithFilesDto(t, files);
    }


    @Transactional
    public TicketDto update(Long id, TicketUpdateDto dto) {

        // 1. Текущий пользователь
        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        // 2. Тикет
        Ticket t = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + id));

        // 3. Проверка прав
        if (!t.getRequester().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Нет прав на изменение этого тикета");
        }


        String oldTitle = t.getTitle();
        String oldDescription = t.getDescription();
        Subcategory oldSubcategory = t.getSubcategory();
        TicketPriority oldPriority = t.getPriority();


        if (dto.title() != null && !dto.title().equals(oldTitle)) {
            t.setTitle(dto.title());

            historyService.record(
                    t,
                    TicketHistoryAction.TITLE_CHANGED,
                    oldTitle,
                    dto.title(),
                    "Изменён заголовок",
                    currentUser
            );
        }


        if (dto.locationId() != null) {

            Location newLocation = locationRepository.findById(dto.locationId())
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "Location not found: " + dto.locationId()
                            )
                    );

            Location oldLocation = t.getLocation();

            // сравнение по id
            if (oldLocation == null || !oldLocation.getId().equals(newLocation.getId())) {

                t.setLocation(newLocation);

                historyService.record(
                        t,
                        TicketHistoryAction.LOCATION_CHANGED,
                        oldLocation != null ? oldLocation.getId().toString() : null,
                        newLocation.getId().toString(),
                        "Изменена локация",
                        currentUser
                );
            }
        }


        if (dto.subcategoryId() != null) {
            Subcategory newSubcategory = subcategoryRepository.findById(dto.subcategoryId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Subcategory not found: " + dto.subcategoryId())
                    );

            if (oldSubcategory == null
                    || !newSubcategory.getId().equals(oldSubcategory.getId())) {

                t.setSubcategory(newSubcategory);

                historyService.record(
                        t,
                        TicketHistoryAction.CATEGORY_CHANGED,
                        oldSubcategory != null ? oldSubcategory.getName() : null,
                        newSubcategory.getName(),
                        "Изменена категория",
                        currentUser
                );
            }
        }


        if (dto.priority() != null && dto.priority() != oldPriority) {
            t.setPriority(dto.priority());

            historyService.record(
                    t,
                    TicketHistoryAction.PRIORITY_CHANGED,
                    oldPriority.name(),
                    dto.priority().name(),
                    "Изменён приоритет",
                    currentUser
            );
        }


        if (dto.description() != null && !dto.description().equals(oldDescription)) {
            t.setDescription(dto.description());

            historyService.record(
                    t,
                    TicketHistoryAction.COMMENT_ADDED,
                    null,
                    null,
                    "Описание заявки обновлено",
                    currentUser
            );
        }

        // 4. Сохранение
        Ticket saved = ticketRepository.save(t);
        return TicketMapper.toDto(saved);
    }

    @Transactional
    public TicketDto update(Long id, TicketUpdateDto dto, List<MultipartFile> files) {
        TicketDto updated = update(id, dto);
        if (files == null || files.isEmpty()) return updated;
        Ticket t = ticketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket not found: " + id));
        String email = currentUserService.getCurrentUserEmail();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));

        for (MultipartFile f : files) {
            if (f.isEmpty()) continue;

            String storageKey = fileStorageService.save(f, t.getId());

            TicketAttachment att = new TicketAttachment();
            att.setTicket(t);
            att.setFilename(f.getOriginalFilename() != null ? f.getOriginalFilename() : "file");
            att.setContentType(f.getContentType() != null ? f.getContentType() : "application/octet-stream");
            att.setSize(f.getSize());
            att.setStorageKey(storageKey);
            att.setUploadedBy(currentUser);

            attachmentRepository.save(att);

            historyService.record(
                    t,
                    TicketHistoryAction.ATTACHMENT_ADDED,
                    null,
                    att.getFilename(),
                    "Добавлено вложение",
                    currentUser
            );
        }
        return TicketMapper.toDto(t);
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
        if (!ticket.getRequester().getId().equals(currentUser.getId())) {
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
