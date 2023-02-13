package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto getCategory(int catId);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto updateCategory(int catId, CategoryDto categoryDto);

    void deleteCategory(int catId);

}
