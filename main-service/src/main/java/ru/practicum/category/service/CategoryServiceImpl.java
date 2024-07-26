package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryNewDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.DataConflictException;
import ru.practicum.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;


    @Override
    @Transactional
    public CategoryDto add(CategoryNewDto categoryNewDto) {
        Category category = CategoryMapper.toNewCategoryDto(categoryNewDto);

        if (categoryRepository.existsByName(category.getName())) {
            throw new DataConflictException(Category.class, String.valueOf(category.getId()),
                    "Категория с name = " + category.getName() + " уже существует");
        }

        Category saveCategory = categoryRepository.save(category);
        return CategoryMapper.toCategoryDto(saveCategory);
    }

    @Override
    @Transactional
    public void deleteById(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException(Category.class, String.valueOf(categoryId),
                    "Категория с id = " + categoryId + " не найдена.");
        }

        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new DataConflictException(Category.class, String.valueOf(categoryId),
                    "Категория с id = " + categoryId + " не может быть удалена, так как к ней привязаны события.");
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public CategoryDto update(Long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, String.valueOf(categoryId),
                        "Категория с id = " + categoryId + " не найдена."));

        if (!Objects.equals(category.getName(), categoryDto.getName()) && categoryRepository.existsByName(categoryDto.getName())) {
            throw new DataConflictException(CategoryDto.class, String.valueOf(categoryDto.getId()),
                    "Категория с name = " + categoryDto.getName() + " уже существует");
        }

        category.setName(categoryDto.getName());

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> findList(Long from, Long size) {
        PageRequest pageRequest = PageRequest.of(Math.toIntExact(from / size), Math.toIntExact(size));
        return categoryRepository.findAll(pageRequest)
                .stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(Category.class, String.valueOf(categoryId),
                        "Категория с id = " + categoryId + " не найдена."));
        return CategoryMapper.toCategoryDto(category);
    }

}