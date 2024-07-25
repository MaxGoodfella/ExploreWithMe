package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryNewDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto add(@Valid @RequestBody CategoryNewDto categoryNewDto) {
        log.info("Start saving category {}", categoryNewDto);
        CategoryDto savedCategory = categoryService.add(categoryNewDto);
        log.info("Start saving category {}", categoryNewDto);
        return savedCategory;
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long categoryId) {
        log.info("Start deleting category with id = {}", categoryId);
        categoryService.deleteById(categoryId);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto update(@PathVariable Long categoryId,
                              @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Start updating category with id = {}", categoryId);
        CategoryDto updatedCategory = categoryService.update(categoryId, categoryDto);
        log.info("Finish updating category with id = {}", updatedCategory.getId());
        return updatedCategory;
    }

}