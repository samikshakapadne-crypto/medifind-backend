package com.medifind.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InventoryRequest {

    @NotNull
    private Long pharmacyId;

    @NotNull
    private Long medicineId;

    @NotBlank
    private String batchNumber;

    @NotNull
    @Min(0)
    private Integer quantity;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal sellingPrice;

    @NotNull
    private LocalDate expiryDate;

    private Integer minimumStockLevel = 10;
}