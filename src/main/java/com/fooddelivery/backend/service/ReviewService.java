package com.fooddelivery.backend.service;

import com.fooddelivery.backend.dto.ReviewRequest;
import com.fooddelivery.backend.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    Page<Review> getReviewsForRestaurant(Long restaurantId, Pageable pageable);
    Page<Review> getReviewsForFood(Long foodId, Pageable pageable);
    Review addReview(Long userId, ReviewRequest reviewRequest);
}
