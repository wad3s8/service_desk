package com.wad3s.service_desk.repository;

import com.wad3s.service_desk.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("""
        select distinct c
        from Category c
        left join fetch c.subcategories
        """)
    List<Category> findAllWithSubcategories();

}
