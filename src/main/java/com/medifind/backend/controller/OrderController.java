package com.medifind.backend.controller;

import com.medifind.backend.dto.request.PlaceOrderRequest;
import com.medifind.backend.dto.response.OrderResponse;
import com.medifind.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            Authentication authentication,
            @Valid @RequestBody PlaceOrderRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        orderService.placeOrder(
                                authentication.getName(),
                                request
                        )
                );
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                orderService.getMyOrders(authentication.getName())
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            Authentication authentication,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(
                orderService.getOrderById(
                        authentication.getName(),
                        orderId
                )
        );
    }
}