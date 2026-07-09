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
@Table(name = "restaurants")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cuisine_type", length = 100)
    private String cuisineType;

    @Builder.Default
    private Double rating = 0.0;

    @Column(name = "delivery_time_mins")
    @Builder.Default
    private Integer deliveryTimeMins = 30;

    @Column(name = "distance_km")
    @Builder.Default
    private Double distanceKm = 1.0;

    @Column(name = "delivery_charge")
    @Builder.Default
    private Double deliveryCharge = 0.0;

    @Column(name = "min_order_amount")
    @Builder.Default
    private Double minOrderAmount = 0.0;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String phone;

    @Column(name = "cost_for_two")
    @Builder.Default
    private Integer costForTwo = 300;

    @Column(name = "logo_url")
    private String logoUrl;

    private String offers;

    @Column(name = "is_opened")
    @Builder.Default
    private Boolean isOpened = true;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    @Builder.Default
    private List<RestaurantImage> images = new ArrayList<>();
}
