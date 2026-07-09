package com.fooddelivery.backend.controller;

import com.fooddelivery.backend.entity.Order;
import com.fooddelivery.backend.entity.Restaurant;
import com.fooddelivery.backend.repository.*;
import com.fooddelivery.backend.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
public class AdminAnalyticsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalRestaurants", restaurantRepository.count());
        stats.put("totalFoods", foodRepository.count());
        stats.put("totalReviews", reviewRepository.count());

        // Revenue calculation
        double totalRevenue = orderRepository.findAll().stream()
                .filter(o -> !o.getStatus().equals("CANCELLED"))
                .mapToDouble(Order::getTotalAmount)
                .sum();
        stats.put("totalRevenue", Math.round(totalRevenue * 100.0) / 100.0);

        // Order status breakdown
        Map<String, Long> orderStatusMap = new HashMap<>();
        orderRepository.findAll().forEach(o ->
                orderStatusMap.merge(o.getStatus(), 1L, Long::sum));
        stats.put("orderStatusBreakdown", orderStatusMap);

        // Top restaurant by order count
        Map<String, Long> restaurantOrderCount = new HashMap<>();
        orderRepository.findAll().forEach(o -> {
            String name = o.getRestaurant().getName();
            restaurantOrderCount.merge(name, 1L, Long::sum);
        });
        stats.put("restaurantOrderCounts", restaurantOrderCount);

        // Average order value
        if (orderRepository.count() > 0) {
            stats.put("averageOrderValue", Math.round(totalRevenue / orderRepository.count() * 100.0) / 100.0);
        } else {
            stats.put("averageOrderValue", 0.0);
        }

        return ResponseEntity.ok(ApiResponse.success("Dashboard analytics retrieved", stats));
    }
}
