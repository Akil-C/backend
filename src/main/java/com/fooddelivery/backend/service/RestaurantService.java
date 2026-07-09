package com.fooddelivery.backend.service;

import com.fooddelivery.backend.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantService {
    Page<Restaurant> getAllRestaurants(Pageable pageable);
    Page<Restaurant> getFilteredRestaurants(
            String search,
            Long categoryId,
            String cuisine,
            Boolean vegOnly,
            Double minRating,
            Integer maxDeliveryTime,
            String priceRange,
            String sort,
            Pageable pageable);
    Page<Restaurant> searchRestaurants(String query, Pageable pageable);
    Page<Restaurant> getRestaurantsByCuisine(String cuisine, Pageable pageable);
    Restaurant getRestaurantById(Long id);
    Restaurant createRestaurant(Restaurant restaurant);
    Restaurant updateRestaurant(Long id, Restaurant restaurantDetails);
    void deleteRestaurant(Long id);
}
