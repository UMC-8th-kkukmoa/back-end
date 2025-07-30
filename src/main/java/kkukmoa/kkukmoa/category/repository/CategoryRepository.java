package kkukmoa.kkukmoa.category.repository;

import kkukmoa.kkukmoa.category.domain.Category;
<<<<<<<< HEAD:src/main/java/kkukmoa/kkukmoa/category/repository/CategoryRepository.java
import kkukmoa.kkukmoa.category.domain.CategoryType;
========
>>>>>>>> dev:src/main/java/kkukmoa/kkukmoa/store/repository/CategoryRepository.java

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
<<<<<<<< HEAD:src/main/java/kkukmoa/kkukmoa/category/repository/CategoryRepository.java
    Optional<Category> findByType(CategoryType type);
========

    Optional<Category> findByName(String name);
>>>>>>>> dev:src/main/java/kkukmoa/kkukmoa/store/repository/CategoryRepository.java
}
