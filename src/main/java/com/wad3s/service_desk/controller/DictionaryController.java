package com.wad3s.service_desk.controller;


import com.wad3s.service_desk.domain.LocationType;
import com.wad3s.service_desk.dto.CategoryDto;
import com.wad3s.service_desk.dto.LocationDto;
import com.wad3s.service_desk.dto.SubcategoryDto;
import com.wad3s.service_desk.repository.CategoryRepository;
import com.wad3s.service_desk.repository.LocationRepository;
import com.wad3s.service_desk.repository.SubcategoryRepository;
import com.wad3s.service_desk.sla.SubcategorySlaListItemDto;
import com.wad3s.service_desk.sla.SubcategorySlaListService;
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
    private final LocationRepository locationRepository;
    private final SubcategorySlaListService subcategorySlaListService;

    @GetMapping("/subcategories")
    public List<SubcategorySlaListItemDto> listSubcategoriesSla() {
        return subcategorySlaListService.list();
    }

    @GetMapping("/locations/offices")
    public List<LocationDto> getOfficeLocations() {
        return locationRepository.findAllByType(LocationType.OFFICE)
                .stream()
                .map(l -> new LocationDto(l.getId(), l.getName()))
                .toList();
    }


    @GetMapping("/categories")
    public List<CategoryDto> getCategories() {
        return categoryRepository.findAllWithSubcategories()
                .stream()
                .map(category -> new CategoryDto(
                        category.getId(),
                        category.getName(),
                        category.getSubcategories()
                                .stream()
                                .map(sc -> new SubcategoryDto(
                                        sc.getId(),
                                        sc.getName()
                                ))
                                .toList()
                ))
                .toList();
    }
}

