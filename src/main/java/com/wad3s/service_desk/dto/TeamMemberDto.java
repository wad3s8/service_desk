package com.wad3s.service_desk.dto;

import com.wad3s.service_desk.domain.TeamRole;

public record TeamMemberDto(
        Long userId,
        String fullName,
        String email,
        TeamRole role
) {}
