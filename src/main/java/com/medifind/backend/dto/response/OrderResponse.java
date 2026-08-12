package com.medifind.backend.dto.response;

import com.medifind.backend.enums.OrderStatus;
import com.medifind.backend.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String orderNumber;
    private Long pharmacyId;
    private String pharmacyName;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private PaymentMethod paymentMethod;
    private String deliveryAddress;
    private LocalDateTime placedAt;
    private List<OrderItemResponse> items;
}