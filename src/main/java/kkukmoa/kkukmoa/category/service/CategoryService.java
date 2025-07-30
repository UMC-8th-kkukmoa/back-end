package kkukmoa.kkukmoa.category.service;

import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.domain.CategoryType;

public interface CategoryService {
    Category getOrCreateCategory(CategoryType type);
}
