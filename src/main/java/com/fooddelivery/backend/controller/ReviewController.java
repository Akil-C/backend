package com.fooddelivery.backend.controller;

import com.fooddelivery.backend.dto.ReviewRequest;
import com.fooddelivery.backend.entity.Review;
import com.fooddelivery.backend.response.ApiResponse;
import com.fooddelivery.backend.security.UserPrincipal;
import com.fooddelivery.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/public/restaurants/{restaurantId}/reviews")
    public ResponseEntity<ApiResponse<Page<Review>>> getRestaurantReviews(
            @PathVariable Long restaurantId, Pageable pageable) {
        Page<Review> reviews = reviewService.getReviewsForRestaurant(restaurantId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved", reviews));
    }

    @GetMapping("/public/foods/{foodId}/reviews")
    public ResponseEntity<ApiResponse<Page<Review>>> getFoodReviews(
            @PathVariable Long foodId, Pageable pageable) {
        Page<Review> reviews = reviewService.getReviewsForFood(foodId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved", reviews));
    }

    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<Review>> addReview(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ReviewRequest reviewRequest) {
        Review review = reviewService.addReview(userPrincipal.getId(), reviewRequest);
        return ResponseEntity.ok(ApiResponse.success("Review submitted successfully", review));
    }
}
