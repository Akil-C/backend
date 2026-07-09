package com.fooddelivery.backend.controller;

import com.fooddelivery.backend.entity.Category;
import com.fooddelivery.backend.response.ApiResponse;
import com.fooddelivery.backend.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Public API endpoints
    @GetMapping("/public/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> list = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", list));
    }

    @GetMapping("/public/categories/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }

    // Admin API endpoints
    @PostMapping("/admin/categories")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return ResponseEntity.ok(ApiResponse.success("Category created successfully", created));
    }

    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Long id, @Valid @RequestBody Category categoryDetails) {
        Category updated = categoryService.updateCategory(id, categoryDetails);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updated));
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }
}
