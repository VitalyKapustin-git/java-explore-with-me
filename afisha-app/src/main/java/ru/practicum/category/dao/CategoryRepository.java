package ru.practicum.category.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Category getCategoryById(int catId);

    void removeCategoryById(int catId);

    Category getCategoryByName(String catName);

}
