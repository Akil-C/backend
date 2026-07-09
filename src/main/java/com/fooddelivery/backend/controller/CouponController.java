package com.fooddelivery.backend.controller;

import com.fooddelivery.backend.entity.Coupon;
import com.fooddelivery.backend.exception.ResourceNotFoundException;
import com.fooddelivery.backend.repository.CouponRepository;
import com.fooddelivery.backend.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CouponController {

    @Autowired
    private CouponRepository couponRepository;

    @GetMapping("/public/coupons")
    public ResponseEntity<ApiResponse<List<Coupon>>> getActiveCoupons() {
        List<Coupon> coupons = couponRepository.findAll().stream()
                .filter(c -> c.getIsActive())
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Active coupons retrieved", coupons));
    }

    @GetMapping("/public/coupons/validate")
    public ResponseEntity<ApiResponse<Coupon>> validateCoupon(@RequestParam("code") String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon code not found: " + code));
        return ResponseEntity.ok(ApiResponse.success("Coupon is valid", coupon));
    }

    @PostMapping("/admin/coupons")
    public ResponseEntity<ApiResponse<Coupon>> createCoupon(@Valid @RequestBody Coupon coupon) {
        Coupon saved = couponRepository.save(coupon);
        return ResponseEntity.ok(ApiResponse.success("Coupon created successfully", saved));
    }

    @PutMapping("/admin/coupons/{id}")
    public ResponseEntity<ApiResponse<Coupon>> updateCoupon(
            @PathVariable Long id, @Valid @RequestBody Coupon couponDetails) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id " + id));
        coupon.setCode(couponDetails.getCode());
        coupon.setDiscountPercentage(couponDetails.getDiscountPercentage());
        coupon.setDiscountAmount(couponDetails.getDiscountAmount());
        coupon.setMaxDiscountAmount(couponDetails.getMaxDiscountAmount());
        coupon.setMinOrderValue(couponDetails.getMinOrderValue());
        coupon.setExpiryDate(couponDetails.getExpiryDate());
        coupon.setIsActive(couponDetails.getIsActive());
        Coupon updated = couponRepository.save(coupon);
        return ResponseEntity.ok(ApiResponse.success("Coupon updated successfully", updated));
    }

    @DeleteMapping("/admin/coupons/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCoupon(@PathVariable Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with id " + id));
        couponRepository.delete(coupon);
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted successfully"));
    }
}
