package com.medifind.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CartItemResponse {

    private Long cartItemId;
    private Long inventoryId;
    private Long medicineId;
    private String medicineName;
    private String strength;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}