package com.medifind.backend.dto.response;

import com.medifind.backend.enums.PrescriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PrescriptionResponse {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long pharmacyId;
    private String pharmacyName;
    private String prescriptionUrl;
    private PrescriptionStatus status;
    private String rejectionReason;
    private LocalDateTime uploadedAt;
    private LocalDateTime reviewedAt;
}