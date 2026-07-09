package com.fooddelivery.backend.controller;

import com.fooddelivery.backend.entity.Food;
import com.fooddelivery.backend.response.ApiResponse;
import com.fooddelivery.backend.service.FoodService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FoodController {

    @Autowired
    private FoodService foodService;

    // Public API endpoints
    @GetMapping("/public/foods")
    public ResponseEntity<ApiResponse<Page<Food>>> getAllFoods(
            @RequestParam(value = "restaurantId", required = false) Long restaurantId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "search", required = false) String search,
            Pageable pageable) {

        Page<Food> page;
        if (restaurantId != null) {
            page = foodService.getFoodsByRestaurant(restaurantId, pageable);
        } else if (categoryId != null) {
            page = foodService.getFoodsByCategory(categoryId, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            page = foodService.searchFoods(search, pageable);
        } else {
            page = foodService.getAllFoods(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success("Foods retrieved successfully", page));
    }

    @GetMapping("/public/foods/{id}")
    public ResponseEntity<ApiResponse<Food>> getFoodById(@PathVariable Long id) {
        Food food = foodService.getFoodById(id);
        return ResponseEntity.ok(ApiResponse.success("Food retrieved successfully", food));
    }

    @GetMapping("/public/restaurants/{restaurantId}/menu")
    public ResponseEntity<ApiResponse<List<Food>>> getRestaurantMenu(
            @PathVariable Long restaurantId,
            @RequestParam(value = "search", required = false) String search) {
        List<Food> menu;
        if (search != null && !search.trim().isEmpty()) {
            menu = foodService.searchMenu(restaurantId, search);
        } else {
            menu = foodService.getMenuByRestaurant(restaurantId);
        }
        return ResponseEntity.ok(ApiResponse.success("Menu retrieved successfully", menu));
    }

    // Admin API endpoints
    @PostMapping("/admin/foods")
    public ResponseEntity<ApiResponse<Food>> createFood(@Valid @RequestBody Food food) {
        Food created = foodService.createFood(food);
        return ResponseEntity.ok(ApiResponse.success("Food created successfully", created));
    }

    @PutMapping("/admin/foods/{id}")
    public ResponseEntity<ApiResponse<Food>> updateFood(
            @PathVariable Long id, @Valid @RequestBody Food foodDetails) {
        Food updated = foodService.updateFood(id, foodDetails);
        return ResponseEntity.ok(ApiResponse.success("Food updated successfully", updated));
    }

    @DeleteMapping("/admin/foods/{id}")
    public ResponseEntity<ApiResponse<String>> deleteFood(@PathVariable Long id) {
        foodService.deleteFood(id);
        return ResponseEntity.ok(ApiResponse.success("Food deleted successfully"));
    }
}
