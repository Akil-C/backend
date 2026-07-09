package com.fooddelivery.backend.service.impl;

import com.fooddelivery.backend.entity.Favorite;
import com.fooddelivery.backend.entity.Food;
import com.fooddelivery.backend.entity.Restaurant;
import com.fooddelivery.backend.entity.User;
import com.fooddelivery.backend.exception.BadRequestException;
import com.fooddelivery.backend.exception.ResourceNotFoundException;
import com.fooddelivery.backend.repository.FavoriteRepository;
import com.fooddelivery.backend.repository.FoodRepository;
import com.fooddelivery.backend.repository.RestaurantRepository;
import com.fooddelivery.backend.repository.UserRepository;
import com.fooddelivery.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Override
    public List<Favorite> getFavoritesByUser(Long userId) {
        return favoriteRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Favorite addRestaurantToFavorites(Long userId, Long restaurantId) {
        if (isRestaurantFavorite(userId, restaurantId)) {
            throw new BadRequestException("Restaurant is already in favorites");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Favorite favorite = Favorite.builder()
                .user(user)
                .restaurant(restaurant)
                .build();
        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public Favorite addFoodToFavorites(Long userId, Long foodId) {
        if (isFoodFavorite(userId, foodId)) {
            throw new BadRequestException("Food item is already in favorites");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        Favorite favorite = Favorite.builder()
                .user(user)
                .food(food)
                .build();
        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void removeRestaurantFromFavorites(Long userId, Long restaurantId) {
        Favorite favorite = favoriteRepository.findByUserIdAndRestaurantId(userId, restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite restaurant relation not found"));
        favoriteRepository.delete(favorite);
    }

    @Override
    @Transactional
    public void removeFoodFromFavorites(Long userId, Long foodId) {
        Favorite favorite = favoriteRepository.findByUserIdAndFoodId(userId, foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite food relation not found"));
        favoriteRepository.delete(favorite);
    }

    @Override
    public Boolean isRestaurantFavorite(Long userId, Long restaurantId) {
        return favoriteRepository.existsByUserIdAndRestaurantId(userId, restaurantId);
    }

    @Override
    public Boolean isFoodFavorite(Long userId, Long foodId) {
        return favoriteRepository.existsByUserIdAndFoodId(userId, foodId);
    }
}
