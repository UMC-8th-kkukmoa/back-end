package kkukmoa.kkukmoa.category.service;

import kkukmoa.kkukmoa.category.domain.Category;

public interface CategoryService {
    Category getOrCreateCategory(String name);
}
