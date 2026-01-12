package com.wad3s.service_desk.sla;

import com.wad3s.service_desk.domain.Subcategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TicketSlaService {

    private final SubcategorySlaRepository slaRepo;

    private static final long DEFAULT_RESOLVE_MINUTES = 24 * 60;

    public Instant calcResolveDueAt(Subcategory subcategory, Instant createdAt) {
        long minutes = DEFAULT_RESOLVE_MINUTES;

        if (subcategory != null && subcategory.getId() != null) {
            minutes = slaRepo.findBySubcategoryId(subcategory.getId())
                    .map(SubcategorySla::getResolveMinutes)
                    .orElse(DEFAULT_RESOLVE_MINUTES);
        }

        return createdAt.plusSeconds(minutes * 60);
    }
}
