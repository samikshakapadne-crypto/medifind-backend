package com.medifind.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MedicineResponse {

    private Long id;
    private String medicineName;
    private String genericName;
    private String brandName;
    private String manufacturer;
    private String category;
    private String strength;
    private String dosageForm;
    private String composition;
    private String description;
    private boolean prescriptionRequired;
    private boolean rareMedicine;
    private String imageUrl;
    private boolean active;
}