package com.medifind.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "pharmacy_inventory",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"pharmacy_id", "medicine_id", "batch_number"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class PharmacyInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @Column(name = "batch_number", nullable = false, length = 100)
    private String batchNumber;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "selling_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "minimum_stock_level", nullable = false)
    private Integer minimumStockLevel = 10;

    @Column(nullable = false)
    private boolean active = true;
}