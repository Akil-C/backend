package com.fooddelivery.backend.controller;

import com.fooddelivery.backend.entity.Address;
import com.fooddelivery.backend.response.ApiResponse;
import com.fooddelivery.backend.security.UserPrincipal;
import com.fooddelivery.backend.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Address>>> getMyAddresses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Address> list = addressService.getAddressesByUser(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved", list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Address>> getAddressById(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long id) {
        Address address = addressService.getAddressById(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Address details retrieved", address));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Address>> createAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @Valid @RequestBody Address address) {
        Address created = addressService.createAddress(userPrincipal.getId(), address);
        return ResponseEntity.ok(ApiResponse.success("Address added successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Address>> updateAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody Address addressDetails) {
        Address updated = addressService.updateAddress(userPrincipal.getId(), id, addressDetails);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long id) {
        addressService.deleteAddress(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully"));
    }
}
