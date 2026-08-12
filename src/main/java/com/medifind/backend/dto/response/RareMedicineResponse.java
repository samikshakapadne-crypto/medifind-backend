package com.medifind.backend.dto.response;

import com.medifind.backend.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RareMedicineResponse {

    private Long id;
    private String medicineName;
    private String genericName;
    private String strength;
    private RequestStatus status;
    private LocalDateTime requestedAt;
}