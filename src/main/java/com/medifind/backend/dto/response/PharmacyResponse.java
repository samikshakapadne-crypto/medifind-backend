package com.medifind.backend.dto.response;

import com.medifind.backend.enums.PharmacyApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PharmacyResponse {

    private Long id;
    private String pharmacyName;
    private String ownerName;
    private String city;
    private PharmacyApprovalStatus approvalStatus;
}