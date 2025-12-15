package com.wad3s.service_desk.repository;

import com.wad3s.service_desk.domain.TeamMember;
import com.wad3s.service_desk.domain.TeamRole;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository
        extends JpaRepository<TeamMember, Long> {

    /**
     * Ищем активных исполнителей с нужным skill
     */
    @Query("""
        select distinct tm
        from TeamMember tm
        join tm.skills s
        where tm.team.id = :teamId
          and tm.role = 'AGENT'
          and tm.active = true
          and s.subcategory.id = :subcategoryId
    """)
    List<TeamMember> findAgentsBySkill(
            @Param("teamId") Long teamId,
            @Param("subcategoryId") Long subcategoryId
    );

    /**
     * Руководитель команды (fallback)
     */
    Optional<TeamMember> findFirstByTeamIdAndRoleAndActiveTrue(
            Long teamId,
            TeamRole role
    );

    @EntityGraph(attributePaths = "team")
    Optional<TeamMember> findByUserIdAndActiveTrue(Long userId);
}
