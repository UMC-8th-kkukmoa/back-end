package kkukmoa.kkukmoa.category.service;

import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryCommandService {

    private final CategoryRepository categoryRepository;

    public Category getOrCreateCategory(CategoryType type) {
        return categoryRepository.findByType(type)
                .orElseGet(() -> createIfAbsent(type));
    }

    private Category createIfAbsent(CategoryType type) {
        try {
            return categoryRepository.save(Category.builder().type(type).build());
        } catch (DataIntegrityViolationException e) {
            return categoryRepository.findByType(type)
                    .orElseThrow(() -> e);
        }
    }
}
