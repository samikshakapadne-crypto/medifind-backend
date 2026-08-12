package com.medifind.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RareMedicineRequestDto {

    @NotBlank
    private String medicineName;

    @NotBlank
    private String genericName;

    @NotBlank
    private String strength;

    private String description;
}