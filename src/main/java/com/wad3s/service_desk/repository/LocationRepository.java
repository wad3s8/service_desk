package com.wad3s.service_desk.repository;

import com.wad3s.service_desk.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByParentId(Long parentId);

    Optional<Location> findByIdAndActiveTrue(Long id);

    @Query("""
        select l
        from Location l
        where l.parent.id = :parentId
          and l.active = true
    """)
    List<Location> findActiveChildren(@Param("parentId") Long parentId);
}
