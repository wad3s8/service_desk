package com.wad3s.service_desk.service;

import com.wad3s.service_desk.domain.*;
import com.wad3s.service_desk.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final TeamMemberRepository teamMemberRepository;

    /**
     * Подбор исполнителя:
     * 1) AGENT с нужным skill
     * 2) fallback → MANAGER
     */
    @Transactional(readOnly = true)
    public User findAssignee(
            Team team,
            Subcategory subcategory
    ) {

        // 1. Ищем исполнителей по skill
        List<TeamMember> agents =
                teamMemberRepository.findAgentsBySkill(
                        team.getId(),
                        subcategory.getId()
                );

        if (!agents.isEmpty()) {
            // MVP: первый найденный
            return agents.get(0).getUser();
        }

        // 2. fallback → руководитель
        return teamMemberRepository
                .findFirstByTeamIdAndRoleAndActiveTrue(
                        team.getId(),
                        TeamRole.MANAGER
                )
                .map(TeamMember::getUser)
                .orElse(null);
    }
}

