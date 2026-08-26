package com.medifind.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "medicines")
@Getter
@Setter
@NoArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medicine_name", nullable = false, length = 150)
    private String medicineName;

    @Column(name = "generic_name", length = 150)
    private String genericName;

    @Column(name = "brand_name", length = 150)
    private String brandName;

    @Column(nullable = false, length = 150)
    private String manufacturer;

    // Main category
    // Example: Cardiovascular, Infections, Dermatology
    @Column(nullable = false, length = 100)
    private String category;

    @Column(length = 50)
    private String strength;

    @Column(name = "dosage_form", nullable = false, length = 50)
    private String dosageForm;

    @Column(length = 500)
    private String composition;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "prescription_required", nullable = false)
    private boolean prescriptionRequired;

    @Column(name = "rare_medicine", nullable = false)
    private boolean rareMedicine;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private boolean active = true;
}