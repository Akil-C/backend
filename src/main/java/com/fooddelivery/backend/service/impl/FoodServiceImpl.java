package com.fooddelivery.backend.service.impl;

import com.fooddelivery.backend.entity.Food;
import com.fooddelivery.backend.exception.ResourceNotFoundException;
import com.fooddelivery.backend.repository.FoodRepository;
import com.fooddelivery.backend.service.FoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FoodServiceImpl implements FoodService {

    @Autowired
    private FoodRepository foodRepository;

    @Override
    public Page<Food> getAllFoods(Pageable pageable) {
        return foodRepository.findAll(pageable);
    }

    @Override
    public Page<Food> getFoodsByRestaurant(Long restaurantId, Pageable pageable) {
        return foodRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId, pageable);
    }

    @Override
    public List<Food> getMenuByRestaurant(Long restaurantId) {
        return foodRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId);
    }

    @Override
    public Page<Food> getFoodsByCategory(Long categoryId, Pageable pageable) {
        return foodRepository.findByCategoryIdAndIsAvailableTrue(categoryId, pageable);
    }

    @Override
    public Page<Food> searchFoods(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            return getAllFoods(pageable);
        }
        return foodRepository.searchFoods(query.trim(), pageable);
    }

    @Override
    public List<Food> searchMenu(Long restaurantId, String query) {
        if (query == null || query.trim().isEmpty()) {
            return getMenuByRestaurant(restaurantId);
        }
        return foodRepository.searchMenu(restaurantId, query.trim());
    }

    @Override
    public Food getFoodById(Long id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found with id " + id));
    }

    @Override
    @Transactional
    public Food createFood(Food food) {
        return foodRepository.save(food);
    }

    @Override
    @Transactional
    public Food updateFood(Long id, Food foodDetails) {
        Food food = getFoodById(id);
        food.setName(foodDetails.getName());
        food.setDescription(foodDetails.getDescription());
        food.setPrice(foodDetails.getPrice());
        food.setIsVeg(foodDetails.getIsVeg());
        food.setIsAvailable(foodDetails.getIsAvailable());
        food.setCategory(foodDetails.getCategory());
        food.setRestaurant(foodDetails.getRestaurant());
        return foodRepository.save(food);
    }

    @Override
    @Transactional
    public void deleteFood(Long id) {
        Food food = getFoodById(id);
        foodRepository.delete(food);
    }
}
