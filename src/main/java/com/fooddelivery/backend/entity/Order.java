package com.fooddelivery.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private Double subtotal;

    @Column(name = "tax_amount", nullable = false)
    private Double taxAmount;

    @Column(name = "delivery_charge")
    @Builder.Default
    private Double deliveryCharge = 0.0;

    @Column(name = "platform_fee")
    @Builder.Default
    private Double platformFee = 2.0;

    @Column(name = "discount_amount")
    @Builder.Default
    private Double discountAmount = 0.0;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Builder.Default
    private String status = "PLACED"; // PLACED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED

    @Column(name = "delivery_address", nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "delivery_partner_id")
    private DeliveryPartner deliveryPartner;

    @Column(name = "coupon_code", length = 20)
    private String couponCode;

    private String notes;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Payment payment;
}
