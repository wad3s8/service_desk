package com.wad3s.service_desk.sla;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubcategorySlaRepository extends JpaRepository<SubcategorySla, Long> {
    Optional<SubcategorySla> findBySubcategoryId(Long subcategoryId);
}


