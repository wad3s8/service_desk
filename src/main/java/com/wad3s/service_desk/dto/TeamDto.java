package com.wad3s.service_desk.dto;

import java.util.List;

public record TeamDto(
        Long id,
        String name,
        String code,
        boolean active,
        List<TeamMemberDto> members
) {}
