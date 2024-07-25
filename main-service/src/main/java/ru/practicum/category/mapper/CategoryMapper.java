package ru.practicum.category.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryNewDto;
import ru.practicum.category.model.Category;


@UtilityClass
public class CategoryMapper {

    public Category toNewCategoryDto(CategoryNewDto categoryNewDto) {
        return Category.builder()
                .name(categoryNewDto.getName())
                .build();
    }

    public CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

}