package com.medifind.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class InventoryResponse {

    private Long inventoryId;

    private Long pharmacyId;
    private String pharmacyName;
    private String city;

    private Long medicineId;
    private String medicineName;
    private String genericName;
    private String strength;

    private String batchNumber;
    private Integer quantity;
    private BigDecimal sellingPrice;
    private LocalDate expiryDate;
    private Integer minimumStockLevel;
    private boolean lowStock;
    private boolean active;
}