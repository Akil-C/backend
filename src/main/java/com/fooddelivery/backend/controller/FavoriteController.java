package com.fooddelivery.backend.controller;

import com.fooddelivery.backend.entity.Favorite;
import com.fooddelivery.backend.response.ApiResponse;
import com.fooddelivery.backend.security.UserPrincipal;
import com.fooddelivery.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Favorite>>> getMyFavorites(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Favorite> list = favoriteService.getFavoritesByUser(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Favorites retrieved", list));
    }

    @PostMapping("/restaurants/{restaurantId}")
    public ResponseEntity<ApiResponse<Favorite>> addRestaurantFavorite(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long restaurantId) {
        Favorite fav = favoriteService.addRestaurantToFavorites(userPrincipal.getId(), restaurantId);
        return ResponseEntity.ok(ApiResponse.success("Restaurant added to favorites", fav));
    }

    @PostMapping("/foods/{foodId}")
    public ResponseEntity<ApiResponse<Favorite>> addFoodFavorite(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long foodId) {
        Favorite fav = favoriteService.addFoodToFavorites(userPrincipal.getId(), foodId);
        return ResponseEntity.ok(ApiResponse.success("Food added to favorites", fav));
    }

    @DeleteMapping("/restaurants/{restaurantId}")
    public ResponseEntity<ApiResponse<String>> removeRestaurantFavorite(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long restaurantId) {
        favoriteService.removeRestaurantFromFavorites(userPrincipal.getId(), restaurantId);
        return ResponseEntity.ok(ApiResponse.success("Restaurant removed from favorites"));
    }

    @DeleteMapping("/foods/{foodId}")
    public ResponseEntity<ApiResponse<String>> removeFoodFavorite(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long foodId) {
        favoriteService.removeFoodFromFavorites(userPrincipal.getId(), foodId);
        return ResponseEntity.ok(ApiResponse.success("Food removed from favorites"));
    }

    @GetMapping("/check/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<Boolean>> checkRestaurantFavorite(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long restaurantId) {
        Boolean isFav = favoriteService.isRestaurantFavorite(userPrincipal.getId(), restaurantId);
        return ResponseEntity.ok(ApiResponse.success("Checked favorite status", isFav));
    }

    @GetMapping("/check/food/{foodId}")
    public ResponseEntity<ApiResponse<Boolean>> checkFoodFavorite(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long foodId) {
        Boolean isFav = favoriteService.isFoodFavorite(userPrincipal.getId(), foodId);
        return ResponseEntity.ok(ApiResponse.success("Checked favorite status", isFav));
    }
}
