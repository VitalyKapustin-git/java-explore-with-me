package ru.practicum.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.core.exceptions.BadRequestException;
import ru.practicum.core.exceptions.ConflictException;
import ru.practicum.core.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {

        if (categoryRepository.getCategoryByName(categoryDto.getName()) != null)
            throw new ConflictException("Category already exists");

        Category category = CategoryMapper.toCategory(categoryDto);
        categoryRepository.save(category);

        categoryDto.setId(category.getId());

        return categoryDto;

    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategory(int catId) {

        Category category = categoryRepository.getCategoryById(catId);

        if (category == null)
            throw new NotFoundException("Category with id=" + catId + " was not found.");

        return CategoryMapper.toCategoryDto(categoryRepository.getCategoryById(catId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(int from, int size) {

        int fromPage = from / size;
        Pageable pageable = PageRequest.of(fromPage, size);

        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public CategoryDto updateCategory(int catId, CategoryDto categoryDto) {

        String newName = categoryDto.getName();
        if (newName == null) throw new BadRequestException("Non valid body");
        if (categoryRepository.getCategoryByName(newName) != null)
            throw new ConflictException("Category already exists");

        categoryDto.setId(catId);

        Category category = CategoryMapper.toCategory(categoryDto);
        categoryRepository.save(category);

        return categoryDto;

    }

    @Override
    @Transactional
    public void deleteCategory(int catId) {
        checkCategory(catId);
        categoryRepository.removeCategoryById(catId);
    }

    private void checkCategory(Integer catId) {
        if (getCategory(catId) == null)
            throw new NotFoundException("Category with id=" + catId + " was not found.");
    }


}
