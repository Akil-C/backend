package com.fooddelivery.backend.controller;

import com.fooddelivery.backend.entity.Restaurant;
import com.fooddelivery.backend.response.ApiResponse;
import com.fooddelivery.backend.service.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    // Public API endpoints
    @GetMapping("/public/restaurants")
    public ResponseEntity<ApiResponse<Page<Restaurant>>> getAllRestaurants(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "cuisine", required = false) String cuisine,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "vegOnly", required = false) Boolean vegOnly,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxDeliveryTime", required = false) Integer maxDeliveryTime,
            @RequestParam(value = "priceRange", required = false) String priceRange,
            @RequestParam(value = "sort", required = false) String sort,
            Pageable pageable) {
        
        // If any filter parameter is provided, use the dynamic filter path
        boolean hasFilters = search != null || cuisine != null || categoryId != null ||
                vegOnly != null || minRating != null || maxDeliveryTime != null ||
                priceRange != null || sort != null;

        Page<Restaurant> page;
        if (hasFilters) {
            page = restaurantService.getFilteredRestaurants(
                    search, categoryId, cuisine, vegOnly, minRating,
                    maxDeliveryTime, priceRange, sort, pageable);
        } else {
            page = restaurantService.getAllRestaurants(pageable);
        }
        return ResponseEntity.ok(ApiResponse.success("Restaurants retrieved successfully", page));
    }

    @GetMapping("/public/restaurants/{id}")
    public ResponseEntity<ApiResponse<Restaurant>> getRestaurantById(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(ApiResponse.success("Restaurant details retrieved successfully", restaurant));
    }

    // Admin API endpoints
    @PostMapping("/admin/restaurants")
    public ResponseEntity<ApiResponse<Restaurant>> createRestaurant(@Valid @RequestBody Restaurant restaurant) {
        Restaurant created = restaurantService.createRestaurant(restaurant);
        return ResponseEntity.ok(ApiResponse.success("Restaurant created successfully", created));
    }

    @PutMapping("/admin/restaurants/{id}")
    public ResponseEntity<ApiResponse<Restaurant>> updateRestaurant(
            @PathVariable Long id, @Valid @RequestBody Restaurant restaurantDetails) {
        Restaurant updated = restaurantService.updateRestaurant(id, restaurantDetails);
        return ResponseEntity.ok(ApiResponse.success("Restaurant updated successfully", updated));
    }

    @DeleteMapping("/admin/restaurants/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok(ApiResponse.success("Restaurant deleted successfully"));
    }
}
