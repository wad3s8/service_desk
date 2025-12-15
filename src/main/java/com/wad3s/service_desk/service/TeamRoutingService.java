package com.wad3s.service_desk.service;

import com.wad3s.service_desk.domain.Location;
import com.wad3s.service_desk.domain.Team;
import com.wad3s.service_desk.repository.LocationRepository;
import com.wad3s.service_desk.repository.TeamLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamRoutingService {

    private final TeamLocationRepository teamLocationRepository;
    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public Optional<Team> findTeamForLocation(Long locationId) {

        Location current = locationRepository.findByIdAndActiveTrue(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

        while (current != null) {

            List<Team> teams =
                    teamLocationRepository.findPrimaryTeamsByLocation(current.getId());

            if (!teams.isEmpty()) {
                return Optional.of(teams.get(0)); // MVP: первая
            }

            current = current.getParent();
        }

        return Optional.empty();
    }
}
