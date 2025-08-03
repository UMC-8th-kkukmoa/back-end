package kkukmoa.kkukmoa.category.seeder;

import kkukmoa.kkukmoa.category.domain.Category;
import kkukmoa.kkukmoa.category.domain.CategoryType;
import kkukmoa.kkukmoa.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategorySeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        for (CategoryType type : CategoryType.values()) {
            categoryRepository
                    .findByType(type)
                    .orElseGet(
                            () -> categoryRepository.save(Category.builder().type(type).build()));
        }
    }
}
