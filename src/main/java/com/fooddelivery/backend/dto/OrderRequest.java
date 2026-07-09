package com.fooddelivery.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {

    @NotNull(message = "Delivery address is required")
    private Long addressId;

    private String couponCode;

    @NotNull(message = "Payment method is required")
    private Long paymentMethodId;

    private String notes;
}
