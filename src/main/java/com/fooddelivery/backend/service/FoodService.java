package com.fooddelivery.backend.service;

import com.fooddelivery.backend.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FoodService {
    Page<Food> getAllFoods(Pageable pageable);
    Page<Food> getFoodsByRestaurant(Long restaurantId, Pageable pageable);
    List<Food> getMenuByRestaurant(Long restaurantId);
    Page<Food> getFoodsByCategory(Long categoryId, Pageable pageable);
    Page<Food> searchFoods(String query, Pageable pageable);
    List<Food> searchMenu(Long restaurantId, String query);
    Food getFoodById(Long id);
    Food createFood(Food food);
    Food updateFood(Long id, Food foodDetails);
    void deleteFood(Long id);
}
