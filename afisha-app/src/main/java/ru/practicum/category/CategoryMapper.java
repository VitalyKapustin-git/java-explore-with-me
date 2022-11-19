package ru.practicum.category;

public class CategoryMapper {

    public static Category toCategory(CategoryDto categoryDto) {

        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());

        return category;

    }

    public static CategoryDto toCategoryDto(Category category) {

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());

        return categoryDto;

    }

}
