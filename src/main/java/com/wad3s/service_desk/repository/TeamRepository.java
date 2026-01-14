package com.wad3s.service_desk.repository;

import com.wad3s.service_desk.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    Optional<Team> findByCodeAndActiveTrue(String code);

    @Query("""
        select distinct t
        from Team t
        join TeamMember tm on tm.team = t
        where tm.user.id = :userId
          and tm.active = true
          and t.active = true
    """)
    List<Team> findActiveTeamsByUser(@Param("userId") Long userId);

    List<Team> findAllByActiveTrueOrderByNameAsc();
}
