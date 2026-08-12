package com.medifind.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class OrderItemResponse {

    private Long orderItemId;
    private Long inventoryId;
    private String medicineName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}