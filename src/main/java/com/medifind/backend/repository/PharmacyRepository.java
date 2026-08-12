package com.medifind.backend.repository;

import com.medifind.backend.entity.Pharmacy;
import com.medifind.backend.enums.PharmacyApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PharmacyRepository
        extends JpaRepository<Pharmacy, Long> {

    List<Pharmacy> findByApprovalStatus(
            PharmacyApprovalStatus approvalStatus
    );
}