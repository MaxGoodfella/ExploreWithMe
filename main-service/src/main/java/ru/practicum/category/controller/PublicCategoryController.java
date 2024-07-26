package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;


    @GetMapping
    public List<CategoryDto> findList(@RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                      @RequestParam(defaultValue = "10") @Positive Long size) {
        log.info("Start fetching categories with parameters: from = {} and size = {}", from, size);
        List<CategoryDto> fetchedCategories = categoryService.findList(from, size);
        log.info("Finish fetching categories with parameters: from = {} and size = {}", from, size);
        return fetchedCategories;
    }

    @GetMapping("/{categoryId}")
    public CategoryDto findById(@PathVariable Long categoryId) {
        log.info("Start fetching category with id = {}", categoryId);
        CategoryDto fetchedCategory = categoryService.findById(categoryId);
        log.info("Finish fetching category with id = {}", fetchedCategory.getId());
        return fetchedCategory;
    }

}