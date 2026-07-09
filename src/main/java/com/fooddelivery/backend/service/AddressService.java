package com.fooddelivery.backend.service;

import com.fooddelivery.backend.entity.Address;
import java.util.List;

public interface AddressService {
    List<Address> getAddressesByUser(Long userId);
    Address getAddressById(Long addressId, Long userId);
    Address createAddress(Long userId, Address address);
    Address updateAddress(Long userId, Long addressId, Address addressDetails);
    void deleteAddress(Long userId, Long addressId);
}
