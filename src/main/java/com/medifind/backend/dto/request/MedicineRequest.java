package com.medifind.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MedicineRequest {

    @NotBlank(message = "Medicine name is required")
    @Size(max = 150)
    private String medicineName;

    @Size(max = 150)
    private String genericName;

    @Size(max = 150)
    private String brandName;

    @NotBlank(message = "Manufacturer is required")
    @Size(max = 150)
    private String manufacturer;

    // Main category
    // Example: Infections, Cardiovascular, Dermatology
    @NotBlank(message = "Category is required")
    @Size(max = 100)
    private String category;

    @Size(max = 50)
    private String strength;

    @NotBlank(message = "Dosage form is required")
    @Size(max = 50)
    private String dosageForm;

    @Size(max = 500)
    private String composition;

    private String description;

    private boolean prescriptionRequired;

    private boolean rareMedicine;

    @Size(max = 500)
    private String imageUrl;
}