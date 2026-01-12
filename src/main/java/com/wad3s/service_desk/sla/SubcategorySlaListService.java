package com.wad3s.service_desk.sla;

import com.wad3s.service_desk.repository.SubcategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubcategorySlaListService {

    private final SubcategoryRepository subcategoryRepository;

    @Transactional(readOnly = true)
    public List<SubcategorySlaListItemDto> list() {
        return subcategoryRepository.fetchSubcategorySlaRows().stream()
                .map(r -> new SubcategorySlaListItemDto(
                        r.getSubcategoryId(),
                        r.getSubcategoryName(),
                        r.getResolveMinutes()
                ))
                .toList();
    }
}

