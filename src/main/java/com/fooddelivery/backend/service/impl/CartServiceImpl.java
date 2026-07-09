package com.fooddelivery.backend.service.impl;

import com.fooddelivery.backend.entity.Cart;
import com.fooddelivery.backend.entity.CartItem;
import com.fooddelivery.backend.entity.Food;
import com.fooddelivery.backend.entity.User;
import com.fooddelivery.backend.exception.BadRequestException;
import com.fooddelivery.backend.exception.ResourceNotFoundException;
import com.fooddelivery.backend.repository.CartItemRepository;
import com.fooddelivery.backend.repository.CartRepository;
import com.fooddelivery.backend.repository.FoodRepository;
import com.fooddelivery.backend.repository.UserRepository;
import com.fooddelivery.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Override
    @Transactional
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    Cart cart = Cart.builder().user(user).items(new ArrayList<>()).build();
                    return cartRepository.save(cart);
                });
    }

    @Override
    @Transactional
    public Cart addToCart(Long userId, Long foodId, Integer quantity, String notes) {
        Cart cart = getCartByUserId(userId);
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        if (!food.getIsAvailable()) {
            throw new BadRequestException("Food item is currently not available");
        }

        // Swiggy behavior: check if restaurant is different from existing items
        if (!cart.getItems().isEmpty()) {
            Long existingRestaurantId = cart.getItems().get(0).getFood().getRestaurant().getId();
            if (!existingRestaurantId.equals(food.getRestaurant().getId())) {
                // Clear cart items from previous restaurant
                cart.getItems().clear();
            }
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getFood().getId().equals(foodId))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setNotes(notes);
        } else {
            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .food(food)
                    .quantity(quantity)
                    .notes(notes)
                    .build();
            cart.getItems().add(cartItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart updateCartItemQuantity(Long userId, Long cartItemId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("This item does not belong to your cart");
        }

        if (quantity <= 0) {
            cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        } else {
            cartItem.setQuantity(quantity);
        }

        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public Cart removeCartItem(Long userId, Long cartItemId) {
        Cart cart = getCartByUserId(userId);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("This item does not belong to your cart");
        }

        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
