package com.fooddelivery.backend.service.impl;

import com.fooddelivery.backend.dto.ReviewRequest;
import com.fooddelivery.backend.entity.Food;
import com.fooddelivery.backend.entity.Restaurant;
import com.fooddelivery.backend.entity.Review;
import com.fooddelivery.backend.entity.User;
import com.fooddelivery.backend.exception.BadRequestException;
import com.fooddelivery.backend.exception.ResourceNotFoundException;
import com.fooddelivery.backend.repository.FoodRepository;
import com.fooddelivery.backend.repository.RestaurantRepository;
import com.fooddelivery.backend.repository.ReviewRepository;
import com.fooddelivery.backend.repository.UserRepository;
import com.fooddelivery.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Override
    public Page<Review> getReviewsForRestaurant(Long restaurantId, Pageable pageable) {
        return reviewRepository.findByRestaurantId(restaurantId, pageable);
    }

    @Override
    public Page<Review> getReviewsForFood(Long foodId, Pageable pageable) {
        return reviewRepository.findByFoodId(foodId, pageable);
    }

    @Override
    @Transactional
    public Review addReview(Long userId, ReviewRequest reviewRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (reviewRequest.getRestaurantId() == null && reviewRequest.getFoodId() == null) {
            throw new BadRequestException("Review must be linked to either a restaurant or a food item");
        }

        Restaurant restaurant = null;
        if (reviewRequest.getRestaurantId() != null) {
            restaurant = restaurantRepository.findById(reviewRequest.getRestaurantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        }

        Food food = null;
        if (reviewRequest.getFoodId() != null) {
            food = foodRepository.findById(reviewRequest.getFoodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
        }

        Review review = Review.builder()
                .user(user)
                .restaurant(restaurant)
                .food(food)
                .rating(reviewRequest.getRating())
                .comment(reviewRequest.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);

        // Recalculate and update restaurant average rating
        if (restaurant != null) {
            updateRestaurantRating(restaurant.getId());
        }

        return savedReview;
    }

    private void updateRestaurantRating(Long restaurantId) {
        List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);
        if (!reviews.isEmpty()) {
            double total = 0.0;
            for (Review r : reviews) {
                total += r.getRating();
            }
            double average = total / reviews.size();
            
            // Round to 1 decimal place
            average = Math.round(average * 10.0) / 10.0;

            Restaurant restaurant = restaurantRepository.findById(restaurantId).get();
            restaurant.setRating(average);
            restaurantRepository.save(restaurant);
        }
    }
}
