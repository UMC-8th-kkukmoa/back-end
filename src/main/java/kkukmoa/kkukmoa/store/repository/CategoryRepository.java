package kkukmoa.kkukmoa.store.repository;

import java.util.Optional;
import kkukmoa.kkukmoa.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  Optional<Category> findByName(String name);

}
