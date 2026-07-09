package com.fooddelivery.backend.controller;

import com.fooddelivery.backend.entity.Cart;
import com.fooddelivery.backend.response.ApiResponse;
import com.fooddelivery.backend.security.UserPrincipal;
import com.fooddelivery.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<Cart>> getCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Cart cart = cartService.getCartByUserId(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cart));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Cart>> addToCart(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("foodId") Long foodId,
            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
            @RequestParam(value = "notes", required = false) String notes) {
        Cart cart = cartService.addToCart(userPrincipal.getId(), foodId, quantity, notes);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", cart));
    }

    @PutMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse<Cart>> updateCartItemQuantity(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long cartItemId,
            @RequestParam("quantity") Integer quantity) {
        Cart cart = cartService.updateCartItemQuantity(userPrincipal.getId(), cartItemId, quantity);
        return ResponseEntity.ok(ApiResponse.success("Cart item quantity updated", cart));
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<ApiResponse<Cart>> removeCartItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long cartItemId) {
        Cart cart = cartService.removeCartItem(userPrincipal.getId(), cartItemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cart));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<String>> clearCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        cartService.clearCart(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully"));
    }
}
