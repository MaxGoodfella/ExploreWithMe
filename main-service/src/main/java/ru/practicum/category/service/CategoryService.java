package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryNewDto;

import java.util.List;


public interface CategoryService {

    CategoryDto add(CategoryNewDto categoryNewDto);

    void deleteById(Long categoryId);

    CategoryDto update(Long categoryId, CategoryDto categoryDto);

    List<CategoryDto> findList(Long from, Long size);

    CategoryDto findById(Long categoryId);

}