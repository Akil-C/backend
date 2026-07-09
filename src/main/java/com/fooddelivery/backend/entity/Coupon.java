package com.fooddelivery.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "discount_percentage")
    @Builder.Default
    private Integer discountPercentage = 0;

    @Column(name = "discount_amount")
    @Builder.Default
    private Double discountAmount = 0.0;

    @Column(name = "max_discount_amount")
    private Double maxDiscountAmount;

    @Column(name = "min_order_value")
    @Builder.Default
    private Double minOrderValue = 0.0;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
