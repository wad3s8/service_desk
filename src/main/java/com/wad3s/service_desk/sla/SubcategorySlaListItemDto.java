package com.wad3s.service_desk.sla;

public record SubcategorySlaListItemDto(
        Long subcategoryId,
        String subcategoryName,
        Long resolveMinutes // null если не задано
) {}

