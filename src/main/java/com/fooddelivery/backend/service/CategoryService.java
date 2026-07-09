package com.fooddelivery.backend.service;

import com.fooddelivery.backend.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category createCategory(Category category);
    Category updateCategory(Long id, Category categoryDetails);
    void deleteCategory(Long id);
}
