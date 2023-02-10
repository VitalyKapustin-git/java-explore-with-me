package ru.practicum.category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Category getCategoryById(int catId);

    void removeCategoryById(int catId);

    Category getCategoryByName(String catName);

}
