package kkukmoa.kkukmoa.category.service;

import kkukmoa.kkukmoa.category.converter.CategoryConverter;
import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;

    @Override
    public Category getOrCreateCategory(CategoryType type) {
        return categoryRepository.findByType(type)
                .orElseGet(() -> categoryRepository.save(Category.builder().type(type).build()));
    }
}
