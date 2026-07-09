package com.fooddelivery.backend.service;

import com.fooddelivery.backend.dto.OrderRequest;
import com.fooddelivery.backend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Order placeOrder(Long userId, OrderRequest orderRequest);
    Order getOrderById(Long orderId, Long userId);
    Order getOrderByIdAdmin(Long orderId);
    Page<Order> getOrdersByUser(Long userId, Pageable pageable);
    Page<Order> getAllOrders(Pageable pageable);
    Order updateOrderStatus(Long orderId, String status);
}
