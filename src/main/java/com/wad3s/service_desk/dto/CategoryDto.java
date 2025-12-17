package com.wad3s.service_desk.dto;

import java.util.List;

public record CategoryDto(
        Long id,
        String name,
        List<SubcategoryDto> subcategories
) {}
