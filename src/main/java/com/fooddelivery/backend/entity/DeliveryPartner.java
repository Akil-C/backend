package com.fooddelivery.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_partners")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(name = "vehicle_number", nullable = false, unique = true, length = 20)
    private String vehicleNumber;

    @Builder.Default
    private String status = "AVAILABLE"; // AVAILABLE, BUSY, OFFLINE

    @Column(name = "current_lat")
    private Double currentLat;

    @Column(name = "current_lng")
    private Double currentLng;
}
