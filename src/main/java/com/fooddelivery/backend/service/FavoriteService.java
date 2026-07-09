package com.fooddelivery.backend.service;

import com.fooddelivery.backend.entity.Favorite;
import java.util.List;

public interface FavoriteService {
    List<Favorite> getFavoritesByUser(Long userId);
    Favorite addRestaurantToFavorites(Long userId, Long restaurantId);
    Favorite addFoodToFavorites(Long userId, Long foodId);
    void removeRestaurantFromFavorites(Long userId, Long restaurantId);
    void removeFoodFromFavorites(Long userId, Long foodId);
    Boolean isRestaurantFavorite(Long userId, Long restaurantId);
    Boolean isFoodFavorite(Long userId, Long foodId);
}
