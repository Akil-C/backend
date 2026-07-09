package com.fooddelivery.backend.repository;

import com.fooddelivery.backend.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Page<Restaurant> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT DISTINCT r FROM Restaurant r " +
           "LEFT JOIN Food f ON f.restaurant.id = r.id AND f.isAvailable = true " +
           "LEFT JOIN Category c ON f.category.id = c.id " +
           "WHERE r.isActive = true AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.cuisineType) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(f.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Restaurant> searchRestaurants(@Param("query") String query, Pageable pageable);

    Page<Restaurant> findByCuisineTypeAndIsActiveTrue(String cuisineType, Pageable pageable);
}
