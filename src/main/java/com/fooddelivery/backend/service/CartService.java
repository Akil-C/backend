package com.fooddelivery.backend.service;

import com.fooddelivery.backend.entity.Cart;

public interface CartService {
    Cart getCartByUserId(Long userId);
    Cart addToCart(Long userId, Long foodId, Integer quantity, String notes);
    Cart updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity);
    Cart removeCartItem(Long userId, Long cartItemId);
    void clearCart(Long userId);
}
