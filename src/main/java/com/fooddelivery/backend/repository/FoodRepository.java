package com.fooddelivery.backend.repository;

import com.fooddelivery.backend.entity.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    Page<Food> findByRestaurantIdAndIsAvailableTrue(Long restaurantId, Pageable pageable);
    
    List<Food> findByRestaurantIdAndIsAvailableTrue(Long restaurantId);

    Page<Food> findByCategoryIdAndIsAvailableTrue(Long categoryId, Pageable pageable);

    @Query("SELECT f FROM Food f WHERE f.isAvailable = true AND " +
           "(LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Food> searchFoods(@Param("query") String query, Pageable pageable);

    @Query("SELECT f FROM Food f WHERE f.restaurant.id = :restaurantId AND f.isAvailable = true AND " +
           "(LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Food> searchMenu(@Param("restaurantId") Long restaurantId, @Param("query") String query);
}
