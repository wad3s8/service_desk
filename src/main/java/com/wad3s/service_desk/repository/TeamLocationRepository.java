package com.wad3s.service_desk.repository;

import com.wad3s.service_desk.domain.Team;
import com.wad3s.service_desk.domain.TeamLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamLocationRepository
        extends JpaRepository<TeamLocation, Long> {

    @Query("""
        select tl.team
        from TeamLocation tl
        where tl.location.id = :locationId
          and tl.team.active = true
          and (tl.responsibilityType = 'PRIMARY'
               or tl.responsibilityType is null)
    """)
    List<Team> findPrimaryTeamsByLocation(
            @Param("locationId") Long locationId
    );

    boolean existsByTeamIdAndLocationId(Long teamId, Long locationId);
}

