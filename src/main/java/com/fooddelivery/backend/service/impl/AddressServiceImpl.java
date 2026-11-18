package com.fooddelivery.backend.service.impl;

import com.fooddelivery.backend.entity.Address;
import com.fooddelivery.backend.entity.User;
import com.fooddelivery.backend.exception.BadRequestException;
import com.fooddelivery.backend.exception.ResourceNotFoundException;
import com.fooddelivery.backend.repository.AddressRepository;
import com.fooddelivery.backend.repository.UserRepository;
import com.fooddelivery.backend.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Address> getAddressesByUser(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    public Address getAddressById(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id " + addressId));
        if (!address.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to this address");
        }
        return address;
    }

    @Override
    @Transactional
    public Address createAddress(Long userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (address.getLabel() == null || address.getLabel().isBlank()) {
            address.setLabel("Home");
        }
        if (address.getIsDefault() == null) {
            address.setIsDefault(false);
        }
        if (address.getStreetAddress() == null) {
            address.setStreetAddress("");
        }
        if (address.getCity() == null) {
            address.setCity("");
        }
        if (address.getState() == null) {
            address.setState("");
        }
        if (address.getPostalCode() == null) {
            address.setPostalCode("");
        }

        // If this is default, turn off other defaults
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            resetDefaultAddresses(userId);
        }

        address.setUser(user);
        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public Address updateAddress(Long userId, Long addressId, Address addressDetails) {
        Address address = getAddressById(addressId, userId);

        if (Boolean.TRUE.equals(addressDetails.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
            resetDefaultAddresses(userId);
        }

        if (addressDetails.getLabel() != null) address.setLabel(addressDetails.getLabel());
        if (addressDetails.getStreetAddress() != null) address.setStreetAddress(addressDetails.getStreetAddress());
        if (addressDetails.getCity() != null) address.setCity(addressDetails.getCity());
        if (addressDetails.getState() != null) address.setState(addressDetails.getState());
        if (addressDetails.getPostalCode() != null) address.setPostalCode(addressDetails.getPostalCode());
        if (addressDetails.getIsDefault() != null) address.setIsDefault(addressDetails.getIsDefault());

        return addressRepository.save(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = getAddressById(addressId, userId);
        addressRepository.delete(address);
    }

    private void resetDefaultAddresses(Long userId) {
        List<Address> list = addressRepository.findByUserId(userId);
        for (Address addr : list) {
            if (addr.getIsDefault()) {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            }
        }
    }
}
