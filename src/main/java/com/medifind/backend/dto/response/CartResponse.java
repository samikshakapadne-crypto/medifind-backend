package com.medifind.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class CartResponse {

    private Long cartId;
    private Long pharmacyId;
    private String pharmacyName;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
}