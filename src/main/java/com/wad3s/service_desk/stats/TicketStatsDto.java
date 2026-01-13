package com.wad3s.service_desk.stats;

public record TicketStatsDto(
        long createdCount,
        long activeCount,
        long closedCount,
        long closedSlaBreachedCount,
        Double slaCompliancePercent,
        Long avgResolveTimeSeconds
) {}
