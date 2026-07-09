package com.fooddelivery.backend.repository;

import com.fooddelivery.backend.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndRestaurantId(Long userId, Long restaurantId);
    Optional<Favorite> findByUserIdAndFoodId(Long userId, Long foodId);
    Boolean existsByUserIdAndRestaurantId(Long userId, Long restaurantId);
    Boolean existsByUserIdAndFoodId(Long userId, Long foodId);
}
