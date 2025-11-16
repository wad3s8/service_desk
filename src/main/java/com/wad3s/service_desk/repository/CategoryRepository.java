package com.wad3s.service_desk.repository;

import com.wad3s.service_desk.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
