package com.wad3s.service_desk.repository;

import com.wad3s.service_desk.domain.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {

    List<Subcategory> findAllByCategoryId(Long categoryId);

    interface SubcategorySlaRow {
        Long getSubcategoryId();
        String getSubcategoryName();
        Long getResolveMinutes(); // может быть null
    }

    @Query("""
        select
          s.id as subcategoryId,
          s.name as subcategoryName,
          sla.resolveMinutes as resolveMinutes
        from Subcategory s
        left join SubcategorySla sla on sla.subcategory = s
        order by s.id asc
    """)
    List<SubcategorySlaRow> fetchSubcategorySlaRows();
}
