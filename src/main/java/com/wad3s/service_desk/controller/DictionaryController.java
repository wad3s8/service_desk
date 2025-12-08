package com.wad3s.service_desk.controller;

import com.wad3s.service_desk.domain.Category;
import com.wad3s.service_desk.domain.Subcategory;
import com.wad3s.service_desk.repository.CategoryRepository;
import com.wad3s.service_desk.repository.SubcategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/dict")
public class DictionaryController {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;

    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/subcategories")
    public List<Subcategory> getSubcategories() {
        return subcategoryRepository.findAll();
    }
}
