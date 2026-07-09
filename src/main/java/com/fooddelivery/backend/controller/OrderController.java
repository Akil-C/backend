package com.fooddelivery.backend.controller;

import com.fooddelivery.backend.dto.OrderRequest;
import com.fooddelivery.backend.entity.Order;
import com.fooddelivery.backend.response.ApiResponse;
import com.fooddelivery.backend.security.UserPrincipal;
import com.fooddelivery.backend.service.OrderService;
import com.fooddelivery.backend.service.PdfInvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PdfInvoiceService pdfInvoiceService;

    // Customer Endpoints
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<Order>> placeOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody OrderRequest orderRequest) {
        Order order = orderService.placeOrder(userPrincipal.getId(), orderRequest);
        return ResponseEntity.ok(ApiResponse.success("Order placed successfully", order));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        Order order = orderService.getOrderById(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Order details retrieved", order));
    }

    @GetMapping("/orders/history")
    public ResponseEntity<ApiResponse<Page<Order>>> getOrderHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {
        Page<Order> history = orderService.getOrdersByUser(userPrincipal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Order history retrieved", history));
    }

    @GetMapping("/orders/{id}/invoice")
    public ResponseEntity<InputStreamResource> downloadInvoice(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        Order order = orderService.getOrderById(id, userPrincipal.getId());
        ByteArrayInputStream bis = pdfInvoiceService.generateInvoicePdf(order);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=" + order.getOrderNumber() + "-invoice.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    // Admin Endpoints
    @GetMapping("/admin/orders")
    public ResponseEntity<ApiResponse<Page<Order>>> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(ApiResponse.success("All orders retrieved", orders));
    }

    @PutMapping("/admin/orders/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam("status") String status) {
        Order updated = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", updated));
    }
}
