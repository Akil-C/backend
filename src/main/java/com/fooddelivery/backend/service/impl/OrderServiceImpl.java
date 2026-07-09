package com.fooddelivery.backend.service.impl;

import com.fooddelivery.backend.dto.OrderRequest;
import com.fooddelivery.backend.entity.*;
import com.fooddelivery.backend.exception.BadRequestException;
import com.fooddelivery.backend.exception.ResourceNotFoundException;
import com.fooddelivery.backend.repository.*;
import com.fooddelivery.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepository;

    @Autowired
    private SettingRepository settingRepository;

    @Override
    @Transactional
    public Order placeOrder(Long userId, OrderRequest orderRequest) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot place order with an empty cart");
        }

        // Subtotal calculation
        double subtotal = 0.0;
        for (CartItem item : cart.getItems()) {
            subtotal += item.getFood().getPrice() * item.getQuantity();
        }

        // Coupon calculation
        double discount = 0.0;
        if (orderRequest.getCouponCode() != null && !orderRequest.getCouponCode().isEmpty()) {
            Coupon coupon = couponRepository.findByCode(orderRequest.getCouponCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Coupon code not found"));

            if (!coupon.getIsActive() || coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Coupon has expired or is inactive");
            }
            if (subtotal < coupon.getMinOrderValue()) {
                throw new BadRequestException("Minimum order value for coupon is INR " + coupon.getMinOrderValue());
            }

            if (coupon.getDiscountPercentage() > 0) {
                discount = subtotal * (coupon.getDiscountPercentage() / 100.0);
                if (coupon.getMaxDiscountAmount() != null && discount > coupon.getMaxDiscountAmount()) {
                    discount = coupon.getMaxDiscountAmount();
                }
            } else if (coupon.getDiscountAmount() > 0) {
                discount = coupon.getDiscountAmount();
            }
        }

        // Fetch calculations settings
        double taxRate = getSettingDouble("tax_rate_percentage", 5.0);
        double platformFee = getSettingDouble("platform_fee", 2.00);
        double perKmRate = getSettingDouble("delivery_charge_per_km", 10.00);

        Restaurant restaurant = cart.getItems().get(0).getFood().getRestaurant();
        double deliveryCharge = restaurant.getDistanceKm() * perKmRate;
        double taxAmount = (subtotal - discount) * (taxRate / 100.0);
        double totalAmount = subtotal - discount + taxAmount + deliveryCharge + platformFee;

        // Fetch delivery address
        Address address = addressRepository.findById(orderRequest.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new BadRequestException("Selected address does not belong to your account");
        }

        String fullAddressString = String.format("%s, %s, %s - %s",
                address.getStreetAddress(), address.getCity(), address.getState(), address.getPostalCode());

        // Allocate delivery partner
        DeliveryPartner partner = null;
        List<DeliveryPartner> availablePartners = deliveryPartnerRepository.findByStatus("AVAILABLE");
        if (!availablePartners.isEmpty()) {
            partner = availablePartners.get(0);
            partner.setStatus("BUSY");
            deliveryPartnerRepository.save(partner);
        }

        // Fetch payment method
        PaymentMethod paymentMethod = paymentMethodRepository.findById(orderRequest.getPaymentMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));

        // Build Order
        Order order = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .user(cart.getUser())
                .restaurant(restaurant)
                .subtotal(subtotal)
                .taxAmount(taxAmount)
                .deliveryCharge(deliveryCharge)
                .platformFee(platformFee)
                .discountAmount(discount)
                .totalAmount(totalAmount)
                .status("PLACED")
                .deliveryAddress(fullAddressString)
                .deliveryPartner(partner)
                .couponCode(orderRequest.getCouponCode())
                .notes(orderRequest.getNotes())
                .items(new ArrayList<>())
                .build();

        // Map cart items to order items
        for (CartItem item : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .food(item.getFood())
                    .quantity(item.getQuantity())
                    .price(item.getFood().getPrice())
                    .notes(item.getNotes())
                    .build();
            order.getItems().add(orderItem);
        }

        // Create Payment log
        Payment payment = Payment.builder()
                .order(order)
                .amount(totalAmount)
                .paymentMethod(paymentMethod)
                .transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
                .status(paymentMethod.getName().equals("CASH_ON_DELIVERY") ? "PENDING" : "COMPLETED")
                .build();

        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        // Clear user's cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Unauthorized access to order details");
        }
        return order;
    }

    @Override
    public Order getOrderByIdAdmin(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Override
    public Page<Order> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrderByIdAdmin(orderId);
        order.setStatus(status);

        // If order finished (DELIVERED or CANCELLED), release the delivery partner
        if (order.getDeliveryPartner() != null && (status.equals("DELIVERED") || status.equals("CANCELLED"))) {
            DeliveryPartner partner = order.getDeliveryPartner();
            partner.setStatus("AVAILABLE");
            deliveryPartnerRepository.save(partner);
        }

        return orderRepository.save(order);
    }

    private double getSettingDouble(String key, double defaultValue) {
        return settingRepository.findByConfigKey(key)
                .map(setting -> Double.parseDouble(setting.getConfigValue()))
                .orElse(defaultValue);
    }
}
