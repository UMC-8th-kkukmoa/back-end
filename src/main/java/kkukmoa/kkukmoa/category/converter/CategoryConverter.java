package kkukmoa.kkukmoa.category.converter;

import kkukmoa.kkukmoa.category.domain.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

    public Category toCategory(String name){
        return Category.builder()
                .name(name)
                .build();
    }

}
